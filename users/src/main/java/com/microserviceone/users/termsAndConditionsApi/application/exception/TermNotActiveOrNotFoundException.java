package com.microserviceone.users.termsAndConditionsApi.application.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TermNotActiveOrNotFoundException extends RuntimeException{
    private final LoggingService loggingService;

    public TermNotActiveOrNotFoundException(){
        super("Term not found or not active");
        this.loggingService = new LoggingService();
        loggingService.logDebug("Termino no encontrado o no activo");
    }
}