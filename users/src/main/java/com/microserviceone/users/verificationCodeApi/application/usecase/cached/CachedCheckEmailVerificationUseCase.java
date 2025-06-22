package com.microserviceone.users.verificationCodeApi.application.usecase.cached;

import com.microserviceone.users.core.cache.config.CacheConfiguration;
import com.microserviceone.users.core.cache.key.CacheKeyGenerator;
import com.microserviceone.users.core.cache.service.CacheService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ICheckEmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CachedCheckEmailVerificationUseCase implements ICheckEmailVerification {

    @Qualifier("checkEmailVerificationUseCase")
    private final ICheckEmailVerification checkEmailVerificationUseCase;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final LoggingService loggingService;

    @Override
    public void checkEmailVerification(String email) {
        try {
            loggingService.logDebug("CachedCheckEmailVerificationUseCase: Verificando con cache si email está verificado - Email: {}", email);

            String cacheKey = keyGenerator.generateKey("emailVerified", email);

            boolean isVerified = cacheService.getOrCompute(
                    CacheConfiguration.EMAIL_VERIFIED_CACHE,
                    cacheKey,
                    () -> {
                        loggingService.logDebug("CachedCheckEmailVerificationUseCase: Cache miss, ejecutando verificación - Email: {}", email);
                        checkEmailVerificationUseCase.checkEmailVerification(email);
                        return true; // If no exception is thrown, it's verified.
                    },
                    Boolean.class
            ).orElse(false);

            if (!isVerified) {
                // This part should ideally not be reached if the supplier logic is correct
                // and the original use case throws an exception on failure.
                // Re-throwing or handling might be needed depending on desired behavior.
                 checkEmailVerificationUseCase.checkEmailVerification(email);
            }
             loggingService.logDebug("CachedCheckEmailVerificationUseCase: Email verificado (cache) - Email: {}", email);

        } catch (Exception e) {
            loggingService.logError("CachedCheckEmailVerificationUseCase: Error en verificación con cache - Email: {}", email, e);
            // Fallback to direct call, which will propagate the original exception
            checkEmailVerificationUseCase.checkEmailVerification(email);
        }
    }

    public void evictEmailVerification(String email) {
        try {
            loggingService.logInfo("CachedCheckEmailVerificationUseCase: Invalidando cache para verificación de email - Email: {}", email);
            String cacheKey = keyGenerator.generateKey("emailVerified", email);
            cacheService.evict(CacheConfiguration.EMAIL_VERIFIED_CACHE, cacheKey);
            loggingService.logDebug("CachedCheckEmailVerificationUseCase: Cache de verificación de email invalidado - Email: {}", email);
        } catch (Exception e) {
            loggingService.logError("CachedCheckEmailVerificationUseCase: Error al invalidar cache de verificación de email - Email: {}", email, e);
        }
    }
} 