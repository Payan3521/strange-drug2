package com.microserviceone.users.core.rateLimiting.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración automática para habilitar las propiedades de rate limiting
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitAutoConfiguration {
    // Esta clase habilita la carga automática de RateLimitProperties
} 