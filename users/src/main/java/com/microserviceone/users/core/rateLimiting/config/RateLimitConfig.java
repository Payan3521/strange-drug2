package com.microserviceone.users.core.rateLimiting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.microserviceone.users.core.rateLimiting.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer {
    
    private final RateLimitInterceptor rateLimitInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Aplicar rate limiting a todos los endpoints de las 3 APIs
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns(
                "/register/**",      // RegisterController
                "/terms/**",         // TermController
                "/verification/**"   // VerificationCodeController
            )
            .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**")
            .order(1);
    }
} 