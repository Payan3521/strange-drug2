package com.microserviceone.users.verificationCodeApi.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class VerificationCodeConfig {
    
    @Value("${verification.code.expiration.minutes:5}")
    private int codeExpirationMinutes;
    
    @Value("${verification.code.length:6}")
    private int codeLength;
    
    public int getCodeExpirationMinutes() {
        return codeExpirationMinutes;
    }
    
    public int getCodeLength() {
        return codeLength;
    }
} 