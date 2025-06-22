package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateAgeUseCase {
    
    private final LoggingService loggingService;
    
    public void validate(Customer customer) {
        try {
            int age = customer.getAge();
            loggingService.logDebug("Validando edad del cliente: {} años (email: {})", age, customer.getEmail());
            
            if (!customer.isOfLegalAge()) {
                loggingService.logWarning("Cliente rechazado por edad insuficiente: {} años (email: {})", 
                    age, customer.getEmail());
                throw new AgeIllegalException(age);
            }
            
            loggingService.logDebug("Edad validada correctamente: {} años (email: {})", age, customer.getEmail());
            
        } catch (AgeIllegalException e) {
            loggingService.logError("Excepción de edad ilegal para cliente con email: {}", customer.getEmail(), e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("Error inesperado al validar edad para cliente con email: {}", customer.getEmail(), e);
            throw e;
        }
    }
}