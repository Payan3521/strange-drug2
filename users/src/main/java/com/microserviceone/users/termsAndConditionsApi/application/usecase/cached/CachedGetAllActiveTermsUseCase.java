package com.microserviceone.users.termsAndConditionsApi.application.usecase.cached;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllActiveTerms;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class CachedGetAllActiveTermsUseCase implements IGetAllActiveTerms {

    @Qualifier("getAllActiveTermsUseCase")
    private final IGetAllActiveTerms getAllActiveTermsUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    @Override
    public List<TermAndCondition> getAllActiveTerms() {
        try {
            loggingService.logInfo("CachedGetAllActiveTermsUseCase: Obteniendo términos activos con cache");
            
            String cacheKey = keyGenerator.generateActiveTermsKey();
            
            @SuppressWarnings("unchecked")
            List<TermAndCondition> cachedTerms = (List<TermAndCondition>) cacheService.getOrCompute(
                CacheConfiguration.TERMS_ACTIVE_CACHE,
                cacheKey,
                () -> {
                    loggingService.logDebug("CachedGetAllActiveTermsUseCase: Cache miss, ejecutando consulta de términos activos");
                    return getAllActiveTermsUseCase.getAllActiveTerms();
                },
                List.class
            ).orElse(List.of());
            
            loggingService.logInfo("CachedGetAllActiveTermsUseCase: Términos activos obtenidos - Cantidad: {}", cachedTerms.size());
            
            return cachedTerms;
            
        } catch (Exception e) {
            loggingService.logError("CachedGetAllActiveTermsUseCase: Error al obtener términos activos con cache", e);
            // Fallback to direct query
            return getAllActiveTermsUseCase.getAllActiveTerms();
        }
    }

    /**
     * Invalidates the active terms cache
     * Should be called when terms are modified
     */
    public void evictActiveTermsCache() {
        try {
            loggingService.logInfo("CachedGetAllActiveTermsUseCase: Invalidando cache de términos activos");
            
            String cacheKey = keyGenerator.generateActiveTermsKey();
            cacheService.evict(CacheConfiguration.TERMS_ACTIVE_CACHE, cacheKey);
            
            loggingService.logDebug("CachedGetAllActiveTermsUseCase: Cache de términos activos invalidado exitosamente");
            
        } catch (Exception e) {
            loggingService.logError("CachedGetAllActiveTermsUseCase: Error al invalidar cache de términos activos", e);
        }
    }
}