package com.microserviceone.users.core.rateLimiting.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio para obtener el identificador único del usuario
 */
@Service
public class UserIdentifierService {
    
    /**
     * Obtiene el identificador del usuario desde el JWT o usa la IP como fallback
     */
    public String getUserIdentifier(HttpServletRequest request) {
        // Intentar obtener el email del contexto de seguridad (JWT)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() != null && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName(); // Esto es el email del JWT
        }
        
        // Fallback a IP address para usuarios no autenticados
        return getClientIpAddress(request);
    }
    
    /**
     * Obtiene la dirección IP real del cliente considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 