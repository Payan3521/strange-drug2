package com.microserviceone.users.verificationCodeApi.application.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeNotFoundException extends RuntimeException {
    private final LoggingService loggingService;
    
    public CodeNotFoundException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logDebug("El codigo no ha sido encontrado");
    }
}