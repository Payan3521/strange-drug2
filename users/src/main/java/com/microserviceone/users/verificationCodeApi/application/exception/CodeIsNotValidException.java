package com.microserviceone.users.verificationCodeApi.application.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.microserviceone.users.core.logging.LoggingService;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CodeIsNotValidException extends RuntimeException{
    private final LoggingService loggingService;
    
    public CodeIsNotValidException(){
        super("El codigo ya ha sido usado o ha expirado");
        this.loggingService = new LoggingService();
        loggingService.logDebug("El codigo ya ha sido usado o ha expirado");
    }
}