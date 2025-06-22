package com.microserviceone.users.core.cache.monitoring;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheMonitoringService {

    private final CacheService cacheService;
    private final LoggingService loggingService;

    private static final List<String> MONITORED_CACHES = List.of(
        CacheConfiguration.USER_BY_ID_CACHE,
        CacheConfiguration.USER_SEARCH_CACHE,
        CacheConfiguration.TERMS_ACTIVE_CACHE,
        CacheConfiguration.TERMS_BY_TYPE_CACHE,
        CacheConfiguration.TERMS_BY_ID_CACHE,
        CacheConfiguration.TERM_ACCEPTANCE_CACHE
    );

    /**
     * Logs cache statistics every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void logCacheStatistics() {
        try {
            loggingService.logInfo("CacheMonitoringService: Reporte de estadísticas de cache");
            
            MONITORED_CACHES.forEach(cacheName -> {
                String stats = cacheService.getCacheStats(cacheName);
                loggingService.logInfo("CacheMonitoringService: {}", stats);
            });
            
        } catch (Exception e) {
            loggingService.logError("CacheMonitoringService: Error al generar reporte de estadísticas", e);
        }
    }

    /**
     * Generates a comprehensive cache report
     */
    public String generateCacheReport() {
        try {
            loggingService.logDebug("CacheMonitoringService: Generando reporte completo de cache");
            
            StringBuilder report = new StringBuilder();
            report.append("=== CACHE PERFORMANCE REPORT ===\n");
            
            MONITORED_CACHES.forEach(cacheName -> {
                String stats = cacheService.getCacheStats(cacheName);
                report.append(stats).append("\n");
            });
            
            report.append("================================\n");
            
            String reportString = report.toString();
            loggingService.logInfo("CacheMonitoringService: Reporte generado exitosamente");
            
            return reportString;
            
        } catch (Exception e) {
            loggingService.logError("CacheMonitoringService: Error al generar reporte de cache", e);
            return "Error generating cache report: " + e.getMessage();
        }
    }

    /**
     * Checks cache health and logs warnings for poor performance
     */
    @Scheduled(fixedRate = 600000) // 10 minutes
    public void performHealthCheck() {
        try {
            loggingService.logDebug("CacheMonitoringService: Ejecutando chequeo de salud de cache");
            
            MONITORED_CACHES.forEach(this::checkCacheHealth);
            
            loggingService.logDebug("CacheMonitoringService: Chequeo de salud completado");
            
        } catch (Exception e) {
            loggingService.logError("CacheMonitoringService: Error en chequeo de salud de cache", e);
        }
    }

    private void checkCacheHealth(String cacheName) {
        try {
            String stats = cacheService.getCacheStats(cacheName);
            
            // Parse hit rate from stats (this is a simplified example)
            if (stats.contains("Hit Rate:")) {
                String hitRateStr = stats.substring(stats.indexOf("Hit Rate:") + 10);
                hitRateStr = hitRateStr.substring(0, hitRateStr.indexOf("%"));
                
                double hitRate = Double.parseDouble(hitRateStr);
                
                if (hitRate < 50.0) {
                    loggingService.logWarning("CacheMonitoringService: Cache con baja eficiencia - {}: {}% hit rate", 
                        cacheName, hitRate);
                } else if (hitRate > 90.0) {
                    loggingService.logInfo("CacheMonitoringService: Cache con excelente rendimiento - {}: {}% hit rate", 
                        cacheName, hitRate);
                }
            }
            
        } catch (Exception e) {
            loggingService.logError("CacheMonitoringService: Error al analizar salud del cache: {}", cacheName, e);
        }
    }
}
