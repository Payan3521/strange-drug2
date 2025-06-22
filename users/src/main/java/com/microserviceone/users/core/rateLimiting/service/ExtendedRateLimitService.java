package com.microserviceone.users.core.rateLimiting.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.exception.TooManyRequestException;
import com.microserviceone.users.core.rateLimiting.model.RequestCounter;
import lombok.RequiredArgsConstructor;

/**
 * Servicio extendido para manejar rate limits con diferentes períodos de tiempo
 */
@Service
@RequiredArgsConstructor
public class ExtendedRateLimitService {
    
    private final LoggingService loggingService;
    private final BlockService blockService;
    
    // Cache para contadores de 5 minutos
    private final ConcurrentHashMap<String, RequestCounter> fiveMinuteCounters = new ConcurrentHashMap<>();
    
    /**
     * Verifica rate limit para endpoints que requieren 1 solicitud cada 5 minutos
     */
    public void checkFiveMinuteRateLimit(String userIdentifier, String endpoint) {
        String key = userIdentifier + ":5min:" + endpoint;
        
        loggingService.logDebug("Verificando rate limit de 5 minutos - Key: " + key + 
            ", Endpoint: " + endpoint);
        
        // Verificar si el usuario está bloqueado
        if (blockService.isUserBlocked(userIdentifier, endpoint)) {
            var blockInfo = blockService.getBlockInfo(userIdentifier, endpoint);
            loggingService.logWarning("Usuario bloqueado intenta acceder al endpoint " + endpoint + 
                " - Bloqueado hasta: " + blockInfo.getBlockedUntil());
            throw new TooManyRequestException("Usuario bloqueado para este endpoint. Intenta más tarde.");
        }
        
        // Obtener o crear contador
        RequestCounter counter = fiveMinuteCounters.computeIfAbsent(key, k -> {
            loggingService.logDebug("Creando nuevo contador de 5 minutos para key: " + key);
            return new RequestCounter();
        });
        
        // Verificar si el contador necesita ser reiniciado (cada 5 minutos)
        LocalDateTime now = LocalDateTime.now();
        if (counter.getLastReset().plus(5, ChronoUnit.MINUTES).isBefore(now)) {
            counter.reset(now);
            loggingService.logDebug("Contador de 5 minutos reiniciado para key: " + key);
        }
        
        // Verificar límite (máximo 1 solicitud en 5 minutos)
        int currentCount = counter.getCount().get();
        loggingService.logDebug("Contador actual para " + key + ": " + currentCount + "/1");
        
        if (currentCount >= 1) {
            loggingService.logWarning("Rate limit de 5 minutos excedido para usuario: " + userIdentifier + 
                " en endpoint: " + endpoint + " - Intentos: " + currentCount);
            
            // Bloquear usuario
            blockService.blockUser(userIdentifier, endpoint);
            
            throw new TooManyRequestException("Demasiadas solicitudes para este endpoint. Solo se permite 1 solicitud cada 5 minutos.");
        }
        
        // Incrementar contador
        int newCount = counter.increment();
        
        loggingService.logDebug("Request permitida para usuario: " + userIdentifier + 
            " en endpoint: " + endpoint + " - Contador: " + newCount + "/1 (5 minutos)");
    }
} 