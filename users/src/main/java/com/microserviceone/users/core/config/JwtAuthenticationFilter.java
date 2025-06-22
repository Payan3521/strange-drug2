package com.microserviceone.users.core.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviceone.users.core.exception.NoAdminAccessException;
import com.microserviceone.users.core.exception.NoTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Endpoints públicos - no requieren autenticación
        if (isPublicEndpoint(requestURI, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificar que exista el header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, String> error = Map.of("error", "Token no proporcionado");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

            String email = claims.getSubject();
            String rol = claims.get("rol", String.class);

            // Verificar que el usuario tenga rol de ADMIN para todos los endpoints protegidos
            if (!rol.equals("ADMIN")) {
                throw new NoAdminAccessException();
            }

            // Crear el token de autenticación
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (NoTokenException | NoAdminAccessException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            Map<String, String> error = Map.of("error", "Token inválido");
            new ObjectMapper().writeValue(response.getOutputStream(), error);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI, String method) {
        // Endpoints de monitoreo y documentación
        if (requestURI.startsWith("/actuator") || 
            requestURI.startsWith("/swagger-ui") || 
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/api-docs")) {
            return true;
        }

        // Endpoints específicos públicos (solo POST)
        if ("POST".equals(method)) {
            return requestURI.equals("/terms/accept") ||
                   requestURI.equals("/terms/accept/multiple") ||
                   requestURI.equals("/register/customer") ||
                   requestURI.equals("/verification/send") ||
                   requestURI.equals("/verification/check");
        }

        return false;
    }
}