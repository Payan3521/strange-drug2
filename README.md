# Sistema de Microservicios

Este proyecto implementa una arquitectura de microservicios utilizando Spring Boot, Spring Cloud Gateway y Eureka Server.

## 🏗️ Arquitectura

El sistema está compuesto por los siguientes microservicios:

| Microservicio | Puerto | Context Path | Descripción |
|--------------|--------|--------------|-------------|
| Eureka Server | 8081 | /eureka | Servidor de registro y descubrimiento |
| Gateway | 8080 | / | API Gateway central |
| Registro | 8082 | /service-registro | Servicio de registro de usuarios |
| Login | 8084 | /service-login | Servicio de autenticación |
| Compras | 8083 | /service-compras | Servicio de gestión de compras |
| Notificaciones | 8085 | /service-notificaciones | Servicio de notificaciones |
| Pagos | 8086 | /service-pagos | Servicio de procesamiento de pagos |
| Videos | 8087 | /service-videos | Servicio de gestión de videos |

## 📦 Dependencias por Microservicio

### Eureka Server
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### Gateway
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

### Microservicios (Registro, Login, Compras, etc.)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

## 🚀 Inicio Rápido

### Requisitos Previos
- Java 17
- Maven
- IDE (recomendado: IntelliJ IDEA o Eclipse)

### Orden de Inicio
1. Iniciar Eureka Server
2. Iniciar Gateway
3. Iniciar los microservicios restantes

## 🔍 Acceso a los Servicios

### Dashboard de Eureka
- URL: `http://localhost:8081`
- Descripción: Panel de control donde puedes ver todos los servicios registrados y su estado

### API Gateway
- URL Base: `http://localhost:8080`
- Rutas disponibles:
  - Registro: `http://localhost:8080/service-registro/**`
  - Login: `http://localhost:8080/service-login/**`
  - Compras: `http://localhost:8080/service-compras/**`
  - Notificaciones: `http://localhost:8080/service-notificaciones/**`
  - Pagos: `http://localhost:8080/service-pagos/**`
  - Videos: `http://localhost:8080/service-videos/**`

### Acceso Directo a Microservicios
Cada microservicio también puede ser accedido directamente:

- Registro: `http://localhost:8082/service-registro`
- Login: `http://localhost:8084/service-login`
- Compras: `http://localhost:8083/service-compras`
- Notificaciones: `http://localhost:8085/service-notificaciones`
- Pagos: `http://localhost:8086/service-pagos`
- Videos: `http://localhost:8087/service-videos`

## 📡 Endpoints de Prueba

Cada microservicio tiene un endpoint de prueba que devuelve un mensaje HTML indicando que el servicio está funcionando:

### A través del Gateway
- Registro: `http://localhost:8080/service-registro/registro`
- Login: `http://localhost:8080/service-login/login`
- Compras: `http://localhost:8080/service-compras/compras`
- Notificaciones: `http://localhost:8080/service-notificaciones/notificaciones`
- Pagos: `http://localhost:8080/service-pagos/pagos`
- Videos: `http://localhost:8080/service-videos/videos`

### Acceso Directo
- Registro: `http://localhost:8082/service-registro/registro`
- Login: `http://localhost:8084/service-login/login`
- Compras: `http://localhost:8083/service-compras/compras`
- Notificaciones: `http://localhost:8085/service-notificaciones/notificaciones`
- Pagos: `http://localhost:8086/service-pagos/pagos`
- Videos: `http://localhost:8087/service-videos/videos`

Cada endpoint devolverá un mensaje HTML similar a:
```html
<h2>Microservice [nombre-del-servicio] is running</h2>
```

## 🛠️ Tecnologías Utilizadas

- Spring Boot 3.2.3
- Spring Cloud 2023.0.0
- Spring Cloud Gateway
- Netflix Eureka Server
- Spring Cloud Netflix Eureka Client
- Java 17
- Maven

## 📦 Estructura del Proyecto

```
├── eureka/                 # Servidor Eureka
├── gateway/               # API Gateway
├── registro/             # Servicio de Registro
├── login/               # Servicio de Login
├── compras/            # Servicio de Compras
├── notificaciones/    # Servicio de Notificaciones
├── pagos/            # Servicio de Pagos
└── videos/          # Servicio de Videos
```

## 🔄 Flujo de Comunicación

1. Los clientes se comunican con el sistema a través del Gateway
2. El Gateway enruta las peticiones al microservicio correspondiente
3. Los microservicios se registran en Eureka Server
4. Eureka Server mantiene el registro de todos los servicios disponibles

## ⚠️ Notas Importantes

- Asegúrate de que el Eureka Server esté funcionando antes de iniciar los demás servicios
- El Gateway debe iniciarse después del Eureka Server
- Los microservicios pueden iniciarse en cualquier orden después del Gateway
- Todos los servicios están configurados para usar el hostname 'localhost'

## 🔍 Monitoreo

Para verificar que todo funciona correctamente:

1. Abre el dashboard de Eureka (`http://localhost:8081`)
2. Verifica que todos los servicios aparezcan como "UP"
3. Prueba los endpoints de prueba a través del Gateway
4. Verifica los logs de cada servicio para asegurar que no hay errores
