package com.microserviceone.users.core.cache.service;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.application.usecase.cached.CachedGetActiveTermByTypeUseCase;
import com.microserviceone.users.termsAndConditionsApi.application.usecase.cached.CachedGetAllActiveTermsUseCase;
import com.microserviceone.users.termsAndConditionsApi.application.usecase.cached.CachedVerifyTermAcceptanceUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CachedGetAllActiveTermsUseCase cachedGetAllActiveTermsUseCase;
    private final CachedGetActiveTermByTypeUseCase cachedGetActiveTermByTypeUseCase;
    private final CachedVerifyTermAcceptanceUseCase cachedVerifyTermAcceptanceUseCase;
    private final LoggingService loggingService;

    /**
     * Evicts all user-related cache entries
     * Should be called when user data is modified
     
    public void evictUserCache(Long userId) {
        try {
            loggingService.logInfo("CacheEvictionService: Invalidando todos los caches relacionados con usuario - ID: {}", userId);
            
            // Evict specific user cache
            cachedFindByIdUseCase.evictUser(userId);
            
            // Clear search cache as user data might affect search results
            cachedFindByFiltersUseCase.clearSearchCache();
            
            loggingService.logInfo("CacheEvictionService: Caches de usuario invalidados exitosamente - ID: {}", userId);
            
        } catch (Exception e) {
            loggingService.logError("CacheEvictionService: Error al invalidar caches de usuario - ID: {}", userId, e);
        }
    }*/

    /**
     * Evicts all terms-related cache entries
     * Should be called when term data is modified
     */
    public void evictTermsCache() {
        try {
            loggingService.logInfo("CacheEvictionService: Invalidando todos los caches de términos");
            
            cachedGetAllActiveTermsUseCase.evictActiveTermsCache();
            
            loggingService.logInfo("CacheEvictionService: Caches de términos invalidados exitosamente");
            
        } catch (Exception e) {
            loggingService.logError("CacheEvictionService: Error al invalidar caches de términos", e);
        }
    }

    /**
     * Evicts cache for specific term type
     */
    public void evictTermsByType(String type) {
        try {
            loggingService.logInfo("CacheEvictionService: Invalidando cache de términos por tipo - Tipo: {}", type);
            
            cachedGetActiveTermByTypeUseCase.evictTermByType(type);
            
            loggingService.logInfo("CacheEvictionService: Cache de términos por tipo invalidado - Tipo: {}", type);
            
        } catch (Exception e) {
            loggingService.logError("CacheEvictionService: Error al invalidar cache de términos por tipo - Tipo: {}", type, e);
        }
    }

    /**
     * Evicts term acceptance cache when a user accepts a term
     */
    public void evictTermAcceptance(Long userId, Long termId) {
        try {
            loggingService.logInfo("CacheEvictionService: Invalidando cache de aceptación - UserID: {}, TermID: {}", userId, termId);
            
            cachedVerifyTermAcceptanceUseCase.evictTermAcceptance(userId, termId);
            
            loggingService.logInfo("CacheEvictionService: Cache de aceptación invalidado - UserID: {}, TermID: {}", userId, termId);
            
        } catch (Exception e) {
            loggingService.logError("CacheEvictionService: Error al invalidar cache de aceptación - UserID: {}, TermID: {}", 
                userId, termId, e);
        }
    }
}
