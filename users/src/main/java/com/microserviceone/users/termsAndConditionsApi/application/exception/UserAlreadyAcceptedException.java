package com.microserviceone.users.termsAndConditionsApi.application.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyAcceptedException extends RuntimeException{
    private final LoggingService loggingService;
    
    public UserAlreadyAcceptedException(){
        super("User has already accepted this term");
        this.loggingService = new LoggingService();
        loggingService.logDebug("Este termino ya ha sido aceptado por el usuario");
    }
}