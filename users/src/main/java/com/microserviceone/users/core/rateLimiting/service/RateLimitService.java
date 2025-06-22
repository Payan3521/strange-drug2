package com.microserviceone.users.core.rateLimiting.service;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.config.RateLimitProperties;
import com.microserviceone.users.core.rateLimiting.exception.TooManyRequestException;
import com.microserviceone.users.core.rateLimiting.model.RequestCounter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    
    private final LoggingService loggingService;
    private final RateLimitProperties rateLimitProperties;
    private final BlockService blockService;
    private final CounterService counterService;
    
    /**
     * Obtiene el límite configurado para un endpoint específico
     */
    public int getEndpointLimit(String endpointKey) {
        int limit = rateLimitProperties.getEndpointLimit(endpointKey);
        loggingService.logDebug("Límite encontrado para " + endpointKey + ": " + limit);
        return limit;
    }
    
    /**
     * Verifica si una solicitud está permitida según los límites de rate limiting
     */
    public void checkRateLimit(String userIdentifier, String endpoint, int maxRequests) {
        String key = userIdentifier + ":" + endpoint;
        
        loggingService.logDebug("Verificando rate limit - Key: " + key + 
            ", Endpoint: " + endpoint + 
            ", MaxRequests: " + maxRequests);
        
        // Verificar si el usuario está bloqueado para este endpoint específico
        if (blockService.isUserBlocked(userIdentifier, endpoint)) {
            var blockInfo = blockService.getBlockInfo(userIdentifier, endpoint);
            loggingService.logWarning("Usuario bloqueado intenta acceder al endpoint " + endpoint + 
                " - Bloqueado hasta: " + blockInfo.getBlockedUntil());
            throw new TooManyRequestException("Usuario bloqueado para este endpoint. Intenta más tarde.");
        }
        
        // Obtener o crear contador para esta combinación usuario/endpoint
        RequestCounter counter = counterService.getOrCreateCounter(userIdentifier, endpoint);
        
        // Verificar si el contador necesita ser reiniciado
        counterService.resetCounterIfNeeded(counter, key);
        
        // Verificar límite
        int currentCount = counterService.getCurrentCount(counter);
        loggingService.logDebug("Contador actual para " + key + ": " + currentCount + "/" + maxRequests);
        
        if (currentCount >= maxRequests) {
            loggingService.logWarning("Rate limit excedido para usuario: " + userIdentifier + 
                " en endpoint: " + endpoint + " - Intentos: " + currentCount);
            
            // Bloquear usuario solo para este endpoint específico
            blockService.blockUser(userIdentifier, endpoint);
            
            throw new TooManyRequestException("Demasiadas solicitudes para este endpoint, inténtalo más tarde");
        }
        
        // Incrementar contador
        int newCount = counterService.incrementCounter(counter);
        
        loggingService.logDebug("Request permitida para usuario: " + userIdentifier + 
            " en endpoint: " + endpoint + " - Contador: " + newCount + "/" + maxRequests);
    }
}