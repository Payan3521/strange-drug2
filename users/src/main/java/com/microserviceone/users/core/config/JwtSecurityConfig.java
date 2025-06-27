package com.microserviceone.users.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.microserviceone.users.core.logging.LoggingService;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final LoggingService loggingService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomClientFilter customClientFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        loggingService.logInfo("Configurando seguridad de la aplicación");
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos específicos
                .requestMatchers(HttpMethod.POST, "/terms/accept").permitAll()
                .requestMatchers(HttpMethod.POST, "/terms/accept/multiple").permitAll()
                .requestMatchers(HttpMethod.POST, "/register/customer").permitAll()
                .requestMatchers(HttpMethod.POST, "/verification/send").permitAll()
                .requestMatchers(HttpMethod.POST, "/verification/check").permitAll()
                
                // Endpoints de documentación y monitoreo
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                
                // Endpoints de Actuator
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/actuator/prometheus").permitAll()
                .requestMatchers("/actuator/metrics/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // TODOS los demás endpoints requieren rol ADMIN
                .anyRequest().hasRole("ADMIN")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(customClientFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        loggingService.logDebug("Configuración de seguridad completada");
        return http.build();
    }

}