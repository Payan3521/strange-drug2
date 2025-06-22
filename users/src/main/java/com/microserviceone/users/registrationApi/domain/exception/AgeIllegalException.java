package com.microserviceone.users.registrationApi.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgeIllegalException extends RuntimeException {
    private final int age;
    private final LoggingService loggingService;

    public AgeIllegalException(int age) {
        super("La edad " + age + " es ilegal. Debe ser mayor de 18 años.");
        this.age = age;
        this.loggingService = new LoggingService();
        loggingService.logDebug("AgeIllegalException creada para edad: {} años - Stack trace disponible", age);
    }
}