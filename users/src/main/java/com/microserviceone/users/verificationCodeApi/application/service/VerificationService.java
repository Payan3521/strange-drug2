package com.microserviceone.users.verificationCodeApi.application.service;

import com.microserviceone.users.verificationCodeApi.domain.port.in.ICheckEmailVerification;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ISendVerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.in.IVerifyCode;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificationService implements ISendVerificationCode, IVerifyCode, ICheckEmailVerification{

    private final ISendVerificationCode sendVerificationCode;
    private final IVerifyCode verifyCode;
    private final ICheckEmailVerification checkEmailVerification;
    private final LoggingService loggingService;

    @Override
    public boolean verifyCode(String email, String code) {
        try {
            loggingService.logInfo("VerificationService: Verificando código - Email: {}", email);
            
            boolean isValid = verifyCode.verifyCode(email, code);
            
            loggingService.logInfo("VerificationService: Verificación de código completada - Email: {}, Válido: {}", 
                email, isValid);
            
            return isValid;
            
        } catch (Exception e) {
            loggingService.logError("VerificationService: Error al verificar código - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    public void sendCode(String email) {
        try {
            loggingService.logInfo("VerificationService: Enviando código de verificación - Email: {}", email);
            
            sendVerificationCode.sendCode(email);
            
            loggingService.logInfo("VerificationService: Código de verificación enviado exitosamente - Email: {}", email);
            
        } catch (Exception e) {
            loggingService.logError("VerificationService: Error al enviar código de verificación - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    public void checkEmailVerification(String email) {
        try {
            loggingService.logInfo("VerificationService: Verificando estado de verificación de email - Email: {}", email);
            
            checkEmailVerification.checkEmailVerification(email);
            
            loggingService.logInfo("VerificationService: Verificación de email completada - Email: {}", email);
            
        } catch (Exception e) {
            loggingService.logError("VerificationService: Error al verificar estado de email - Email: {}", email, e);
            throw e;
        }
    }
    
}