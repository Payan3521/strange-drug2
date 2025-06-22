package com.microserviceone.users.registrationApi.application.service;

import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationService implements ISaveAdmin, ISaveCustomer{

    private final ISaveAdmin saveAdmin;
    private final ISaveCustomer saveCustomer;
    private final LoggingService loggingService;

    @Override
    public Customer save(Customer customer) {
        try {
            loggingService.logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
            
            Customer savedCustomer = saveCustomer.save(customer);
            
            loggingService.logInfo("RegistrationService: Cliente registrado exitosamente - ID: {}, Email: {}", 
                savedCustomer.getId(), savedCustomer.getEmail());
            
            return savedCustomer;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al registrar cliente - Email: {}", customer.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Admin save(Admin admin) {
        try {
            loggingService.logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
            
            Admin savedAdmin = saveAdmin.save(admin);
            
            loggingService.logInfo("RegistrationService: Administrador registrado exitosamente - ID: {}, Email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
            
            return savedAdmin;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al registrar administrador - Email: {}", admin.getEmail(), e);
            throw e;
        }
    }
    
}