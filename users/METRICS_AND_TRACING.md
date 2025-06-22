# Métricas y Tracing en el Microservicio de Usuarios

## 1. Dependencias Necesarias

En el archivo `pom.xml`, agregar las siguientes dependencias:

```xml
<!-- Observabilidad -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Tracing -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

## 2. Configuración de Prometheus

Crear archivo `prometheus.yml` en `src/main/resources/`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'users-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8082']
```
## 2.1. Agregar configuraciones en properties
```
  # Actuator Configuration
  management.endpoints.web.exposure.include=*
  management.endpoint.health.show-details=always
  management.endpoint.prometheus.enabled=true
  management.endpoints.web.base-path=/actuator
  management.endpoint.health.probes.enabled=true
  management.health.livenessState.enabled=true
  management.health.readinessState.enabled=true

  # Métricas personalizadas
  management.metrics.tags.application=${spring.application.name}
  management.metrics.distribution.percentiles-histogram.http.server.requests=true
  management.metrics.distribution.slo.http.server.requests=10ms, 50ms, 100ms, 200ms, 500ms

  # Tracing Configuration
  management.tracing.sampling.probability=1.0
  management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
```

## 3. Configuración de Contenedores

### 3.1 Crear Red Docker
```bash
docker network create monitoring
```

### 3.2 Iniciar Prometheus
```bash
docker run -d --name prometheus --network monitoring -p 9090:9090 -v ~/Descargas/strange-drug/users/src/main/resources/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
```

### 3.3 Iniciar Grafana
```bash
docker run -d --name grafana --network monitoring -p 3000:3000 grafana/grafana
```

### 3.4 Iniciar Jaeger
```bash
docker run -d --name jaeger --network monitoring -p 16686:16686 -p 14250:14250 jaegertracing/all-in-one:1.22
```

## 4. Acceso a las Interfaces

### 4.1 Prometheus
- URL: `http://localhost:9090`
- Verifica que el target `users-service` esté en estado UP

### 4.2 Grafana
- URL: `http://localhost:3000`
- Credenciales por defecto:
  - Usuario: `admin`
  - Contraseña: `admin`
- Configurar fuente de datos:
  1. Ve a Configuration → Data Sources
  2. Add data source → Prometheus
  3. URL: `http://192.168.1.71:9090` (usar tu IP local)
  4. Save & Test

### 4.3 Jaeger
- URL: `http://localhost:16686`
- Seleccionar servicio "users-service"
- Ver traces de las peticiones

## 5. Verificación de IP Local

Para obtener la IP local (necesaria para Grafana):
```bash
hostname -I
```
Usar la primera IP que aparece (en este caso: 192.168.1.71)

## 6. Métricas Disponibles

### 6.1 Métricas de JVM
- `jvm_memory_used_bytes`: Uso de memoria
- `jvm_threads_live_threads`: Hilos activos
- `jvm_classes_loaded_classes`: Clases cargadas

### 6.2 Métricas de Base de Datos
- `hikaricp_connections`: Conexiones del pool
- `jdbc_connections_active`: Conexiones activas
- `jdbc_connections_idle`: Conexiones inactivas

### 6.3 Métricas de HTTP
- `http_server_requests_active_seconds`: Tiempo de respuesta
- `http_server_requests_active_seconds_max`: Tiempo máximo de respuesta

## 7. Troubleshooting

### 7.1 Prometheus no puede acceder a las métricas
- Verificar que el servicio esté corriendo en el puerto 8082
- Verificar que el endpoint `/actuator/prometheus` sea accesible
- Verificar la configuración de seguridad en `SecurityConfig.java`

### 7.2 Grafana no puede conectarse a Prometheus
- Usar la IP local en lugar de localhost
- Verificar que Prometheus esté corriendo
- Verificar que la red Docker esté configurada correctamente

### 7.3 Jaeger no muestra traces
- Verificar que las dependencias de tracing estén correctamente configuradas
- Verificar que el servicio esté enviando traces
- Verificar que Jaeger esté corriendo correctamente 