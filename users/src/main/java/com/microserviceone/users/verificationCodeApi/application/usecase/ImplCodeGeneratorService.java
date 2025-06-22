package com.microserviceone.users.verificationCodeApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ImplCodeGeneratorService implements CodeGeneratorService{

    private final SecureRandom secureRandom;
    private final LoggingService loggingService;

    @Override
    public String generateCode() {
        try {
            loggingService.logDebug("ImplCodeGeneratorService: Generando código de verificación");
            
            String code = String.format("%06d", secureRandom.nextInt(1000000));
            
            loggingService.logDebug("ImplCodeGeneratorService: Código generado exitosamente: {}", code);
            
            return code;
            
        } catch (Exception e) {
            loggingService.logError("ImplCodeGeneratorService: Error al generar código de verificación", e);
            throw e;
        }
    }

}