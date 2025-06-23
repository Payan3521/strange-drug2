package com.microserviceone.users.registrationApi.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;
import com.microserviceone.users.registrationApi.web.dto.UserResponse;
import com.microserviceone.users.registrationApi.web.webMapper.RegistrationWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.RateLimit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
@Tag(name = "Register", description = "API para el registro de usuarios")
public class RegisterController {
    
    private final RegistrationService registrationService;
    private final RegistrationWebMapper registrationWebMapper;
    private final LoggingService loggingService;

    @Operation(summary = "Registrar cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto: El cliente ya existe"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "register_customer", maxRequests = 10, description = "Registro de clientes - 10 solicitudes por minuto")
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<UserResponse>> registerCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        try {
            loggingService.logInfo("Iniciando registro de cliente - Email: {}, Nombre: {} {}", 
                customerRequest.getEmail(), customerRequest.getName(), customerRequest.getLastName());
            
            // Mapear request a dominio
            loggingService.logDebug("Mapeando CustomerRequest a dominio para email: {}", customerRequest.getEmail());
            Customer customer = registrationWebMapper.toCustomer(customerRequest);
            
            // Registrar cliente
            loggingService.logDebug("Procesando registro de cliente con email: {}", customer.getEmail());
            Customer registeredCustomer = registrationService.save(customer);
            
            // Mapear respuesta
            loggingService.logDebug("Mapeando respuesta para cliente ID: {}", registeredCustomer.getId());
            UserResponse userResponse = registrationWebMapper.toResponse(registeredCustomer);

            loggingService.logInfo("Cliente registrado exitosamente - ID: {}, Email: {}, Rol: {}", 
                userResponse.getId(), userResponse.getEmail(), userResponse.getRol());

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", userResponse));
                
        } catch (Exception e) {
            loggingService.logError("Error al registrar cliente con email: {}", customerRequest.getEmail(), e);
            throw e;
        }
    }
}