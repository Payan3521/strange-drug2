package com.microserviceone.users.termsAndConditionsApi.application.usecase.cached;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetActiveTermByType;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class CachedGetActiveTermByTypeUseCase implements IGetActiveTermByType {

    @Qualifier("getActiveTermByTypeUseCase")
    private final IGetActiveTermByType getActiveTermByTypeUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    @Override
    public Optional<TermAndCondition> getActiveTermByType(String type) {
        try {
            loggingService.logDebug("CachedGetActiveTermByTypeUseCase: Obteniendo término por tipo con cache - Tipo: {}", type);
            
            String cacheKey = keyGenerator.generateTermsByTypeKey(type);
            
            Optional<TermAndCondition> cachedTerm = cacheService.getOrCompute(
                CacheConfiguration.TERMS_BY_TYPE_CACHE,
                cacheKey,
                () -> {
                    loggingService.logDebug("CachedGetActiveTermByTypeUseCase: Cache miss, ejecutando consulta - Tipo: {}", type);
                    return getActiveTermByTypeUseCase.getActiveTermByType(type).orElse(null);
                },
                TermAndCondition.class
            );
            
            if (cachedTerm.isPresent()) {
                loggingService.logDebug("CachedGetActiveTermByTypeUseCase: Término encontrado - Tipo: {}, ID: {}, Título: {}", 
                    type, cachedTerm.get().getId(), cachedTerm.get().getTitle());
            } else {
                loggingService.logDebug("CachedGetActiveTermByTypeUseCase: No se encontró término activo para tipo: {}", type);
            }
            
            return cachedTerm;
            
        } catch (Exception e) {
            loggingService.logError("CachedGetActiveTermByTypeUseCase: Error al obtener término por tipo con cache - Tipo: {}", type, e);
            // Fallback to direct query
            return getActiveTermByTypeUseCase.getActiveTermByType(type);
        }
    }

    /**
     * Invalidates cache entry for a specific term type
     */
    public void evictTermByType(String type) {
        try {
            loggingService.logInfo("CachedGetActiveTermByTypeUseCase: Invalidando cache para tipo - Tipo: {}", type);
            
            String cacheKey = keyGenerator.generateTermsByTypeKey(type);
            cacheService.evict(CacheConfiguration.TERMS_BY_TYPE_CACHE, cacheKey);
            
            loggingService.logDebug("CachedGetActiveTermByTypeUseCase: Cache invalidado exitosamente - Tipo: {}", type);
            
        } catch (Exception e) {
            loggingService.logError("CachedGetActiveTermByTypeUseCase: Error al invalidar cache - Tipo: {}", type, e);
        }
    }
}