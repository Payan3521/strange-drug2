package com.microserviceone.users.termsAndConditionsApi.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TermsNotAcceptedException extends RuntimeException {

    private final LoggingService loggingService;
    
    public TermsNotAcceptedException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logDebug("Termino no ha sido aceptado");
    }
} 