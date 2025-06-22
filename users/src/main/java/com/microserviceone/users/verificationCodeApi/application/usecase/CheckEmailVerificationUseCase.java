package com.microserviceone.users.verificationCodeApi.application.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.verificationCodeApi.application.exception.EmailNotVerifiedException;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ICheckEmailVerification;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("checkEmailVerificationUseCase")
@RequiredArgsConstructor
public class CheckEmailVerificationUseCase implements ICheckEmailVerification{
    
    private final IVerificationCodeRepository verificationCodeRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public void checkEmailVerification(String email) {
        try {
            loggingService.logInfo("CheckEmailVerificationUseCase: Verificando estado de verificación de email - Email: {}", email);
            
            boolean isVerified = verificationCodeRepository.isEmailVerified(email);
            
            if (!isVerified) {
                loggingService.logWarning("CheckEmailVerificationUseCase: Email no verificado - Email: {}", email);
                throw new EmailNotVerifiedException();
            }
            
            loggingService.logInfo("CheckEmailVerificationUseCase: Email verificado correctamente - Email: {}", email);
            
        } catch (EmailNotVerifiedException e) {
            loggingService.logError("CheckEmailVerificationUseCase: Excepción de email no verificado - Email: {}", email, e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("CheckEmailVerificationUseCase: Error inesperado al verificar email - Email: {}", email, e);
            throw e;
        }
    }
} 