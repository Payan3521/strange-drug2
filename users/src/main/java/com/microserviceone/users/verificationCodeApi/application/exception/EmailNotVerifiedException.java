package com.microserviceone.users.verificationCodeApi.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailNotVerifiedException extends RuntimeException {
    private final LoggingService loggingService;

    public EmailNotVerifiedException() {
        super("El correo electrónico aún no ha sido verificado");
        this.loggingService = new LoggingService();
        loggingService.logDebug("El correo electrónico aún no ha sido verificado");
    }
} 