package com.microserviceone.users.verificationCodeApi.application.usecase;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.verificationCodeApi.application.exception.CodeNotFoundException;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyExistCodeUseCase {
    private final IVerificationCodeRepository verificationCodeRepository;
    private final LoggingService loggingService;

    @Transactional(readOnly = true)
    public VerificationCode validateExistCode(String email, String code) {
        try {
            loggingService.logDebug("VerifyExistCodeUseCase: Verificando existencia de código - Email: {}, Código: {}", email, code);
            
            Optional<VerificationCode> verificationCode = verificationCodeRepository.findByEmailAndCode(email, code);
            
            if (!verificationCode.isPresent()) {
                loggingService.logWarning("VerifyExistCodeUseCase: Código no encontrado - Email: {}, Código: {}", email, code);
                throw new CodeNotFoundException("El código ingresado no es correcto");
            }
            
            VerificationCode foundCode = verificationCode.get();
            loggingService.logDebug("VerifyExistCodeUseCase: Código encontrado - Email: {}, Código: {}, ID: {}", 
                email, code, foundCode.getId());
            
            return foundCode;
            
        } catch (CodeNotFoundException e) {
            loggingService.logError("VerifyExistCodeUseCase: Excepción de código no encontrado - Email: {}, Código: {}", email, code, e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("VerifyExistCodeUseCase: Error inesperado al verificar existencia de código - Email: {}, Código: {}", email, code, e);
            throw e;
        }
    }
}