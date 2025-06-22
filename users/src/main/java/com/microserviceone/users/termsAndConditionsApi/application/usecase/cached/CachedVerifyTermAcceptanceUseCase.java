package com.microserviceone.users.termsAndConditionsApi.application.usecase.cached;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyTermAcceptance;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

/**
 * Cached decorator for VerifyTermAcceptanceUseCase
 * Implements caching strategy for term acceptance verification
 */
@Service
@Primary
@RequiredArgsConstructor
public class CachedVerifyTermAcceptanceUseCase implements IVerifyTermAcceptance {

    @Qualifier("verifyTermAcceptanceUseCase")
    private final IVerifyTermAcceptance verifyTermAcceptanceUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    @Override
    public boolean hasAcceptedTerm(Long userId, Long termId) {
        try {
            loggingService.logDebug("CachedVerifyTermAcceptanceUseCase: Verificando aceptación con cache - UserID: {}, TermID: {}", 
                userId, termId);
            
            String cacheKey = keyGenerator.generateTermAcceptanceKey(userId, termId);
            
            Boolean cachedResult = cacheService.getOrCompute(
                CacheConfiguration.TERM_ACCEPTANCE_CACHE,
                cacheKey,
                () -> {
                    loggingService.logDebug("CachedVerifyTermAcceptanceUseCase: Cache miss, ejecutando verificación - UserID: {}, TermID: {}", 
                        userId, termId);
                    return verifyTermAcceptanceUseCase.hasAcceptedTerm(userId, termId);
                },
                Boolean.class
            ).orElse(false);
            
            loggingService.logDebug("CachedVerifyTermAcceptanceUseCase: Verificación completada - UserID: {}, TermID: {}, Aceptado: {}", 
                userId, termId, cachedResult);
            
            return cachedResult;
            
        } catch (Exception e) {
            loggingService.logError("CachedVerifyTermAcceptanceUseCase: Error en verificación con cache - UserID: {}, TermID: {}", 
                userId, termId, e);
            // Fallback to direct query
            return verifyTermAcceptanceUseCase.hasAcceptedTerm(userId, termId);
        }
    }

    /**
     * Invalidates cache entry for term acceptance
     * Should be called when a user accepts a term
     */
    public void evictTermAcceptance(Long userId, Long termId) {
        try {
            loggingService.logInfo("CachedVerifyTermAcceptanceUseCase: Invalidando cache de aceptación - UserID: {}, TermID: {}", 
                userId, termId);
            
            String cacheKey = keyGenerator.generateTermAcceptanceKey(userId, termId);
            cacheService.evict(CacheConfiguration.TERM_ACCEPTANCE_CACHE, cacheKey);
            
            loggingService.logDebug("CachedVerifyTermAcceptanceUseCase: Cache de aceptación invalidado exitosamente - UserID: {}, TermID: {}", 
                userId, termId);
            
        } catch (Exception e) {
            loggingService.logError("CachedVerifyTermAcceptanceUseCase: Error al invalidar cache de aceptación - UserID: {}, TermID: {}", 
                userId, termId, e);
        }
    }
}
