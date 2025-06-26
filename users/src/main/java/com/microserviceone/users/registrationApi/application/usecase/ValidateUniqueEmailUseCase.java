package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("validateUniqueEmailUseCase")
@RequiredArgsConstructor
public class ValidateUniqueEmailUseCase {

    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    public void validate(String email) {
        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email no puede ser nulo o vacío");
        }
        try {
            loggingService.logDebug("Validando unicidad de email: {}", email);
            
            if(registerRepository.existsByEmail(email)) {
                loggingService.logWarning("Email ya registrado en el sistema: {}", email);
                throw new UserAlreadyRegisteredException(email);
            }
            
            loggingService.logDebug("Email validado como único: {}", email);
            
        } catch (UserAlreadyRegisteredException e) {
            loggingService.logError("Excepción de email duplicado: {}", email, e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("Error inesperado al validar email: {}", email, e);
            throw e;
        }
    }
}