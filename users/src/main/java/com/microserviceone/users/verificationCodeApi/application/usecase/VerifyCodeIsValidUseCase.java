package com.microserviceone.users.verificationCodeApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.verificationCodeApi.application.exception.CodeIsNotValidException;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyCodeIsValidUseCase {
    
    private final LoggingService loggingService;
    
    public void validateCodeIsValid(VerificationCode verificationCode){
        try {
            loggingService.logDebug("VerifyCodeIsValidUseCase: Validando código - ID: {}, Email: {}, Código: {}", 
                verificationCode.getId(), verificationCode.getEmail(), verificationCode.getCode());
            
            if(!verificationCode.isValid()){
                loggingService.logWarning("VerifyCodeIsValidUseCase: Código no válido - ID: {}, Email: {}, Código: {}", 
                    verificationCode.getId(), verificationCode.getEmail(), verificationCode.getCode());
                throw new CodeIsNotValidException();
            }
            
            loggingService.logDebug("VerifyCodeIsValidUseCase: Código validado como válido - ID: {}, Email: {}, Código: {}", 
                verificationCode.getId(), verificationCode.getEmail(), verificationCode.getCode());
            
        } catch (CodeIsNotValidException e) {
            loggingService.logError("VerifyCodeIsValidUseCase: Excepción de código no válido - ID: {}, Email: {}, Código: {}", 
                verificationCode.getId(), verificationCode.getEmail(), verificationCode.getCode(), e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("VerifyCodeIsValidUseCase: Error inesperado al validar código - ID: {}, Email: {}, Código: {}", 
                verificationCode.getId(), verificationCode.getEmail(), verificationCode.getCode(), e);
            throw e;
        }
    }
}