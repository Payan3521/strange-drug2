package com.microserviceone.users.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccessErrorException extends RuntimeException{
    public AccessErrorException(){
        super("No tienes acceso en este backend");
    }
}