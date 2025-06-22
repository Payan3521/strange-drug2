package com.microserviceone.users.verificationCodeApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.in.IVerifyCode;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.verificationCodeApi.application.usecase.cached.CachedCheckEmailVerificationUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyCodeUseCase implements IVerifyCode{

    private final VerifyExistCodeUseCase verifyExistCodeUseCase;
    private final VerifyCodeIsValidUseCase verifyCodeIsValidUseCase;
    private final IVerificationCodeRepository verificationCodeRepository;
    private final LoggingService loggingService;
    private final CachedCheckEmailVerificationUseCase cachedCheckEmailVerificationUseCase;

    @Override
    @Transactional
    public boolean verifyCode(String email, String code) {
        try {
            loggingService.logInfo("VerifyCodeUseCase: Iniciando verificación de código - Email: {}, Código: {}", email, code);
            
            // Verificar que el código existe
            loggingService.logDebug("VerifyCodeUseCase: Verificando existencia del código - Email: {}, Código: {}", email, code);
            VerificationCode response = verifyExistCodeUseCase.validateExistCode(email, code);
            loggingService.logDebug("VerifyCodeUseCase: Código encontrado - Email: {}, Código: {}, ID: {}", email, code, response.getId());
            
            // Verificar que el código es válido
            loggingService.logDebug("VerifyCodeUseCase: Validando código - Email: {}, Código: {}", email, code);
            verifyCodeIsValidUseCase.validateCodeIsValid(response);
            loggingService.logDebug("VerifyCodeUseCase: Código validado como válido - Email: {}, Código: {}", email, code);
            
            // Marcar como usado
            loggingService.logDebug("VerifyCodeUseCase: Marcando código como usado - Email: {}, Código: {}", email, code);
            response.markAsUsed();
            verificationCodeRepository.save(response);
            loggingService.logDebug("VerifyCodeUseCase: Código marcado como usado y guardado - Email: {}, Código: {}", email, code);
            
            // Marcar email como verificado
            loggingService.logDebug("VerifyCodeUseCase: Marcando email como verificado - Email: {}, Código: {}", email, code);
            verificationCodeRepository.markCodeAsVerified(email, code);
            
            // Invalidar cache de verificación
            cachedCheckEmailVerificationUseCase.evictEmailVerification(email);
            
            loggingService.logInfo("VerifyCodeUseCase: Verificación de código completada exitosamente - Email: {}, Código: {}", email, code);
            
            return true;
            
        } catch (Exception e) {
            loggingService.logError("VerifyCodeUseCase: Error al verificar código - Email: {}, Código: {}", email, code, e);
            throw e;
        }
    }

}