package com.microserviceone.users.registrationApi.application.usecase.cached;

import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.usecase.ValidateUniqueEmailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CachedValidateUniqueEmailUseCase {

    @Qualifier("validateUniqueEmailUseCase")
    private final ValidateUniqueEmailUseCase validateUniqueEmailUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    public void validate(String email) {
        try {
            loggingService.logDebug("CachedValidateUniqueEmailUseCase: Validando con cache si email es único - Email: {}", email);

            String cacheKey = keyGenerator.generateKey("userExistsByEmail", email);

            cacheService.getOrCompute(
                    CacheConfiguration.USER_EXISTS_BY_EMAIL_CACHE,
                    cacheKey,
                    () -> {
                        loggingService.logDebug("CachedValidateUniqueEmailUseCase: Cache miss, ejecutando validación - Email: {}", email);
                        validateUniqueEmailUseCase.validate(email);
                        // If no exception, the email is unique, we can cache this state.
                        // We cache 'false' to represent "does not exist".
                        return false;
                    },
                    Boolean.class
            );
             loggingService.logDebug("CachedValidateUniqueEmailUseCase: Email validado como único (cache) - Email: {}", email);

        } catch (Exception e) {
            loggingService.logError("CachedValidateUniqueEmailUseCase: Error en validación con cache - Email: {}", email, e);
            // Fallback and propagate exception
            validateUniqueEmailUseCase.validate(email);
        }
    }

    public void evictValidateUniqueEmail(String email) {
        try {
            loggingService.logInfo("CachedValidateUniqueEmailUseCase: Invalidando cache para validación de email - Email: {}", email);
            String cacheKey = keyGenerator.generateKey("userExistsByEmail", email);
            cacheService.evict(CacheConfiguration.USER_EXISTS_BY_EMAIL_CACHE, cacheKey);
            loggingService.logDebug("CachedValidateUniqueEmailUseCase: Cache de validación de email invalidado - Email: {}", email);
        } catch (Exception e) {
            loggingService.logError("CachedValidateUniqueEmailUseCase: Error al invalidar cache de validación de email - Email: {}", email, e);
        }
    }
} 