package com.microserviceone.users.core.rateLimiting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
     /**
     * Número máximo de solicitudes permitidas por minuto
     */
    int maxRequests() default 60;
    
    /**
     * Identificador único para el tipo de rate limit
     */
    String key() default "";
    
    /**
     * Descripción del límite para logging
     */
    String description() default "";
}