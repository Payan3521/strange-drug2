package com.microserviceone.users.verificationCodeApi.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.verificationCodeApi.application.service.VerificationService;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ICheckEmailVerification;
import com.microserviceone.users.verificationCodeApi.domain.port.in.ISendVerificationCode;
import com.microserviceone.users.verificationCodeApi.domain.port.in.IVerifyCode;
import com.microserviceone.users.verificationCodeApi.domain.port.out.IVerificationCodeRepository;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.adapter.AdapterVerificationCode;


@Configuration
public class AppConfigVerificationCode {
    
    @Bean
    @Primary
    public IVerificationCodeRepository verificationCodeRepository(AdapterVerificationCode adapterVerificationCode){
        return adapterVerificationCode;
    }

    @Bean
    public VerificationService verificationService(
            ISendVerificationCode sendVerificationCode, IVerifyCode verifyCode, 
            ICheckEmailVerification checkEmailVerification, LoggingService loggingService
        ){
        return new VerificationService(sendVerificationCode, verifyCode, checkEmailVerification, loggingService);
    }

}