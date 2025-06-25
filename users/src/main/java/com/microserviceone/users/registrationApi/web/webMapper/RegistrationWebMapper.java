package com.microserviceone.users.registrationApi.web.webMapper;

import org.springframework.stereotype.Component;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.web.dto.AdminRequest;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;
import com.microserviceone.users.registrationApi.web.dto.UserResponse;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistrationWebMapper {

    private final LoggingService loggingService;

    public Customer toCustomer(CustomerRequest request) {
        try {
            loggingService.logDebug("Mapeando CustomerRequest a Customer - Email: {}, Nombre: {} {}", 
                request.getEmail(), request.getName(), request.getLastName());
            
            Customer customer = new Customer(
                request.getName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getBirthDate()
            );

            customer.verifyCode();
            customer.verifyTerm();
            
            loggingService.logDebug("Customer mapeado exitosamente - Email: {}, Edad: {} años", 
                customer.getEmail(), customer.getAge());
            
            return customer;
            
        } catch (Exception e) {
            loggingService.logError("Error al mapear CustomerRequest a Customer - Email: {}", request.getEmail(), e);
            throw e;
        }
    }
    
    public Admin toAdmin(AdminRequest request) {
        try {
            loggingService.logDebug("Mapeando AdminRequest a Admin - Email: {}, Nombre: {} {}, Área: {}", 
                request.getEmail(), request.getName(), request.getLastName(), request.getArea());
            
            Admin admin = new Admin(
                request.getName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getArea()
            );
            
            loggingService.logDebug("Admin mapeado exitosamente - Email: {}, Área: {}", 
                admin.getEmail(), admin.getArea());
            
            return admin;
            
        } catch (Exception e) {
            loggingService.logError("Error al mapear AdminRequest a Admin - Email: {}", request.getEmail(), e);
            throw e;
        }
    }
    
    public UserResponse toResponse(User user) {
        try {
            loggingService.logDebug("Mapeando User a UserResponse - ID: {}, Email: {}, Tipo: {}", 
                user.getId(), user.getEmail(), user.getClass().getSimpleName());
            
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setPassword(user.getPassword());
            response.setPhone(user.getPhone());
            response.setRol(user.getRol().name());
            response.setVerifiedCode(user.isVerifiedCode());
            response.setVerifiedTerm(user.isVerifiedTerm());
            
            // Set specific fields based on user type
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                response.setBirthDate(customer.getBirthDate());
                loggingService.logDebug("Campos específicos de Customer agregados - Fecha de nacimiento: {}", 
                    customer.getBirthDate());
            } else if (user instanceof Admin) {
                Admin admin = (Admin) user;
                response.setArea(admin.getArea());
                loggingService.logDebug("Campos específicos de Admin agregados - Área: {}", admin.getArea());
            }
            
            loggingService.logDebug("UserResponse mapeado exitosamente - ID: {}, Email: {}, Rol: {}", 
                response.getId(), response.getEmail(), response.getRol());
            
            return response;
            
        } catch (Exception e) {
            loggingService.logError("Error al mapear User a UserResponse - ID: {}, Email: {}", 
                user.getId(), user.getEmail(), e);
            throw e;
        }
    }
   
}