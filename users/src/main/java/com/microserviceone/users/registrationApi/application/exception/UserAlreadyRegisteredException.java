package com.microserviceone.users.registrationApi.application.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyRegisteredException extends RuntimeException {
    private final LoggingService loggingService;
    private String email;

    public UserAlreadyRegisteredException(String email) {
        super("El usuario con email " + email + " ya está registrado");
        this.email=email;
        this.loggingService = new LoggingService();
        loggingService.logDebug("Usuario ya registrado con - EMAIL: {}", email);
    }
}