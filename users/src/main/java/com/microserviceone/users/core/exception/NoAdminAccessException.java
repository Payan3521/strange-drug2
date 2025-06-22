package com.microserviceone.users.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAdminAccessException extends RuntimeException{
    private final LoggingService loggingService;
    public NoAdminAccessException() {
        super("No tienes permisos de administrador");
        this.loggingService = new LoggingService();
        loggingService.logDebug("No hay acceso de ADMINISTRADOR");
    }
}