package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaveCustomerUseCase implements ISaveCustomer{

    private final ValidateUniqueEmailUseCase validateUniqueEmailUseCase;
    private final ValidateAgeUseCase validateAgeUseCase;
    private final PasswordEncripterUseCase passwordEncripterUseCase;
    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional
    public Customer save(Customer customer) {
        try {
            loggingService.logInfo("Iniciando registro de cliente con email: {} y edad: {}", 
                customer.getEmail(), customer.getAge());
            
            // Validar email único
            loggingService.logDebug("Validando unicidad de email: {}", customer.getEmail());
            validateUniqueEmailUseCase.validate(customer.getEmail());
            loggingService.logDebug("Email validado como único: {}", customer.getEmail());
            
            // Validar edad
            loggingService.logDebug("Validando edad del cliente: {} años", customer.getAge());
            validateAgeUseCase.validate(customer);
            loggingService.logDebug("Edad validada correctamente: {} años", customer.getAge());
            
            // Encriptar contraseña
            loggingService.logDebug("Encriptando contraseña para cliente: {}", customer.getEmail());
            customer.setPassword(passwordEncripterUseCase.encripter(customer.getPassword(), customer.getEmail()));
            
            // Guardar cliente
            loggingService.logDebug("Guardando cliente en base de datos: {}", customer.getEmail());
            Customer savedCustomer = (Customer) registerRepository.save(customer);
            
            loggingService.logInfo("Cliente registrado exitosamente con ID: {}, email: {} y edad: {}", 
                savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getAge());
            
            return savedCustomer;
            
        } catch (Exception e) {
            loggingService.logError("Error al registrar cliente con email: {}", customer.getEmail(), e);
            throw e;
        }
    }

}