package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.PasswordNullOrEmptyException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordEncripterUseCase {

    private final PasswordEncoder passwordEncoder;
    private final LoggingService loggingService;

    public String encripter(String password, String email){
        try {

            if (password == null || password.isEmpty()) {
                loggingService.logError("La contraseña no puede ser nula o vacía para el email: {}", email);
                throw new PasswordNullOrEmptyException();
            }

            loggingService.logInfo("Iniciando encriptación de contraseña para email: {}", email);
            
            String encryptedPassword = passwordEncoder.encode(password);
            
            loggingService.logInfo("Contraseña encriptada exitosamente para email: {}", email);
            loggingService.logDebug("Longitud de contraseña encriptada: {} caracteres", encryptedPassword.length());
            
            return encryptedPassword;
        } catch (Exception e) {
            loggingService.logError("Error al encriptar contraseña para email: {}", email, e);
            throw e;
        }
    }
}