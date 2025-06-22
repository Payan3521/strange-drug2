package com.microserviceone.users.core.rateLimiting.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.model.RequestCounter;
import lombok.RequiredArgsConstructor;

/**
 * Servicio para manejar contadores de requests por usuario/endpoint
 */
@Service
@RequiredArgsConstructor
public class CounterService {
    
    private final LoggingService loggingService;
    
    // Cache para almacenar contadores de requests por usuario/endpoint
    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    
    /**
     * Obtiene o crea un contador para una combinación usuario/endpoint
     */
    public RequestCounter getOrCreateCounter(String userIdentifier, String endpoint) {
        String key = generateKey(userIdentifier, endpoint);
        
        return requestCounters.computeIfAbsent(key, k -> {
            loggingService.logDebug("Creando nuevo contador para key: " + key);
            return new RequestCounter();
        });
    }
    
    /**
     * Verifica si el contador necesita ser reiniciado y lo reinicia si es necesario
     */
    public void resetCounterIfNeeded(RequestCounter counter, String key) {
        LocalDateTime now = LocalDateTime.now();
        
        // Reiniciar contador si ha pasado más de un minuto
        if (counter.getLastReset().plus(1, ChronoUnit.MINUTES).isBefore(now)) {
            counter.reset(now);
            loggingService.logDebug("Contador reiniciado para key: " + key);
        }
    }
    
    /**
     * Incrementa el contador y retorna el nuevo valor
     */
    public int incrementCounter(RequestCounter counter) {
        return counter.increment();
    }
    
    /**
     * Obtiene el valor actual del contador
     */
    public int getCurrentCount(RequestCounter counter) {
        return counter.getCount().get();
    }
    
    /**
     * Genera una clave única para el cache basada en usuario y endpoint
     */
    private String generateKey(String userIdentifier, String endpoint) {
        return userIdentifier + ":" + endpoint;
    }
} 