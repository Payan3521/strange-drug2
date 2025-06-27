package com.microserviceone.users.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import com.microserviceone.users.core.exception.AccessErrorException;

@Component // Marca esta clase como un componente de Spring para que pueda ser inyectada
public class CustomClientFilter extends OncePerRequestFilter {

    // Inyecta el nombre del encabezado secreto desde application.properties
    @Value("${app.client.secret-header-name}")
    private String secretHeaderName;

    // Inyecta el valor secreto desde application.properties
    @Value("${app.client.secret-value}")
    private String secretValue;

    /**
     * Este método se ejecuta por cada petición HTTP.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el valor del encabezado secreto de la petición
        String clientSecret = request.getHeader(secretHeaderName);

        // 2. Verificar si el encabezado está presente y si su valor es el esperado
        // Si el encabezado no está presente O si el valor no coincide con el secreto
        if (clientSecret == null || !clientSecret.equals(secretValue)) {
            // Si no es válido, lanza la excepción personalizada
            throw new AccessErrorException();
        }

        // 3. Si el encabezado es válido, la petición puede continuar
        // Pasa la petición al siguiente filtro en la cadena de Spring Security
        filterChain.doFilter(request, response);
    }
}