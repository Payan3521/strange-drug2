package com.microserviceone.users.core.rateLimiting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * Configuración de rate limiting desde application.properties
 */
@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {
    
    // Configuración de rate limits por endpoint
    private Map<String, Integer> endpointLimits = new HashMap<>();
    
    // Configuración de tiempos de bloqueo progresivo
    private Map<Integer, Integer> blockDurations = new HashMap<>();
    
    // Límite por defecto
    private int defaultLimit = 60;
    
    public RateLimitProperties() {

        // RegisterController - Rate limits
        endpointLimits.put("register_customer", 10); // 10 solicitudes en 1 minuto
        
        // TermController - Rate limits
        endpointLimits.put("terms_get_all_active", 20);      // 20 solicitudes en 1 minuto
        endpointLimits.put("terms_get_by_type", 20);         // 20 solicitudes en 1 minuto
        endpointLimits.put("terms_get_all", 20);             // 20 solicitudes en 1 minuto
        endpointLimits.put("terms_get_by_id", 20);           // 20 solicitudes en 1 minuto
        endpointLimits.put("terms_accept_single", 3);        // 3 solicitudes en 1 minuto
        endpointLimits.put("terms_accept_multiple", 3);      // 3 solicitudes en 1 minuto
        endpointLimits.put("terms_verify_acceptance", 5);    // 5 solicitudes en 1 minuto
        endpointLimits.put("terms_verify_by_email", 5);      // 5 solicitudes en 1 minuto
        
        // VerificationCodeController - Rate limits
        endpointLimits.put("verification_send_code", 1);     // 1 solicitud en 5 minutos (se maneja en el servicio)
        endpointLimits.put("verification_verify_code", 1);   // 1 solicitud en 5 minutos (se maneja en el servicio)
        endpointLimits.put("verification_check_status", 5);  // 5 solicitudes en 1 minuto
        
        // Configuración de bloqueos progresivos
        blockDurations.put(1, 1);   // Primer bloqueo: 1 minuto
        blockDurations.put(2, 5);   // Segundo bloqueo: 5 minutos
        blockDurations.put(3, 10);  // Tercer bloqueo: 10 minutos
        blockDurations.put(4, 15);  // Cuarto bloqueo: 15 minutos
        blockDurations.put(5, 30);  // Quinto bloqueo: 30 minutos
    }
    
    public Map<String, Integer> getEndpointLimits() {
        return endpointLimits;
    }
    
    public void setEndpointLimits(Map<String, Integer> endpointLimits) {
        this.endpointLimits = endpointLimits;
    }
    
    public Map<Integer, Integer> getBlockDurations() {
        return blockDurations;
    }
    
    public void setBlockDurations(Map<Integer, Integer> blockDurations) {
        this.blockDurations = blockDurations;
    }
    
    public int getDefaultLimit() {
        return defaultLimit;
    }
    
    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }
    
    public int getEndpointLimit(String endpointKey) {
        return endpointLimits.getOrDefault(endpointKey, defaultLimit);
    }
    
    public int getBlockDuration(int blockLevel) {
        return blockDurations.getOrDefault(blockLevel, 30); // 30 minutos por defecto
    }
} 