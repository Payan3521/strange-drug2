package com.microserviceone.users.termsAndConditionsApi.application.usecase.cached;

import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetTermById;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class CachedGetTermByIdUseCase implements IGetTermById {

    @Qualifier("getTermByIdUseCase")
    private final IGetTermById getTermByIdUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    @Override
    public Optional<TermAndCondition> getById(Long id) {
        try {
            loggingService.logDebug("CachedGetTermByIdUseCase: Obteniendo término por ID con cache - ID: {}", id);

            String cacheKey = keyGenerator.generateTermByIdKey(id);

            return cacheService.getOrCompute(
                    CacheConfiguration.TERMS_BY_ID_CACHE,
                    cacheKey,
                    () -> {
                        loggingService.logDebug("CachedGetTermByIdUseCase: Cache miss, ejecutando consulta - ID: {}", id);
                        return getTermByIdUseCase.getById(id).orElse(null);
                    },
                    TermAndCondition.class
            );
        } catch (Exception e) {
            loggingService.logError("CachedGetTermByIdUseCase: Error al obtener término por ID con cache - ID: {}", id, e);
            return getTermByIdUseCase.getById(id);
        }
    }

    public void evictTermById(Long id) {
        try {
            loggingService.logInfo("CachedGetTermByIdUseCase: Invalidando cache para término - ID: {}", id);
            String cacheKey = keyGenerator.generateTermByIdKey(id);
            cacheService.evict(CacheConfiguration.TERMS_BY_ID_CACHE, cacheKey);
            loggingService.logDebug("CachedGetTermByIdUseCase: Cache invalidado exitosamente - ID: {}", id);
        } catch (Exception e) {
            loggingService.logError("CachedGetTermByIdUseCase: Error al invalidar cache - ID: {}", id, e);
        }
    }
} 