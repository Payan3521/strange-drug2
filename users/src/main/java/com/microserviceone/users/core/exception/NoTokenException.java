package com.microserviceone.users.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoTokenException extends RuntimeException{
    private final LoggingService loggingService;
    public NoTokenException() {
        super("Debes ingresar un token de acceso");
        this.loggingService = new LoggingService();
        loggingService.logDebug("No se envio token de acceso");
    }
}