package com.microserviceone.users.registrationApi.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import com.microserviceone.users.registrationApi.infraestructure.persistance.adapter.AdapterRegister;

@Configuration
public class AppConfigRegister {

    @Bean
    @Primary
    public IRegisterRepository registerRepository(AdapterRegister adapterRegister){
        return adapterRegister;
    }

    @Bean
    public RegistrationService registrationService(ISaveAdmin saveAdmin, ISaveCustomer saveCustomer, LoggingService loggingService){
        return new RegistrationService(saveAdmin, saveCustomer, loggingService);
    }
}