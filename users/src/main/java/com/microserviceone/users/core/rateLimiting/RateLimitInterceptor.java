package com.microserviceone.users.core.rateLimiting;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.service.RateLimitService;
import com.microserviceone.users.core.rateLimiting.service.ExtendedRateLimitService;
import com.microserviceone.users.core.rateLimiting.service.UserIdentifierService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimitService rateLimitService;
    private final ExtendedRateLimitService extendedRateLimitService;
    private final UserIdentifierService userIdentifierService;
    private final LoggingService loggingService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        
        Method method = handlerMethod.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        if (rateLimit == null) {
            return true;
        }
        
        // Obtener identificador del usuario (email del JWT o IP)
        String userIdentifier = userIdentifierService.getUserIdentifier(request);
        
        // Usar la key definida en la anotación
        String endpointKey = rateLimit.key();
        
        if (endpointKey.isEmpty()) {
            loggingService.logWarning("No se encontró key para el endpoint: " + request.getRequestURI());
            return true;
        }
        
        // Verificar si es un endpoint de 5 minutos
        if (isFiveMinuteEndpoint(endpointKey)) {
            extendedRateLimitService.checkFiveMinuteRateLimit(userIdentifier, endpointKey);
        } else {
            // Obtener el límite configurado para este endpoint (1 minuto por defecto)
            int maxRequests = rateLimitService.getEndpointLimit(endpointKey);
            
            loggingService.logDebug("Verificando rate limit para usuario: " + userIdentifier + 
                " en endpoint: " + endpointKey + " - Límite: " + maxRequests + "/min");
            
            // Verificar rate limit
            rateLimitService.checkRateLimit(userIdentifier, endpointKey, maxRequests);
        }
        
        return true;
    }
    
    /**
     * Determina si un endpoint requiere rate limiting de 5 minutos
     */
    private boolean isFiveMinuteEndpoint(String endpointKey) {
        return endpointKey.equals("verification_send_code") || 
               endpointKey.equals("verification_verify_code");
    }
}