package com.microserviceone.users.termsAndConditionsApi.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.application.service.TermsService;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptTerm;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetActiveTermByType;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllActiveTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetTermById;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyAllTermsAccepted;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyTermAcceptance;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.adapter.AdapterAccepted;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.adapter.AdapterTerm;

@Configuration
public class AppConfigTerms {

    @Bean
    public ITermAcceptanceRepository termAcceptanceRepository(AdapterAccepted adapterAccepted){
        return adapterAccepted;
    }

    @Bean
    public ITermRepository termRepository(AdapterTerm adapterTerm){
        return adapterTerm;
    }

    @Bean
    public TermsService termsService(
            IAcceptTerm acceptTerm, IAcceptAllTerms acceptAllTerms, 
            IGetAllActiveTerms getAllActiveTerms, IGetActiveTermByType getActiveTermByType, 
            IGetTermById getTermById, IGetAllTerms getAllTerms, IVerifyTermAcceptance verifyTermAcceptance, 
            IVerifyAllTermsAccepted verifyAllTermsAccepted, LoggingService loggingService
        ){

        return new TermsService(
            acceptTerm, acceptAllTerms, getAllActiveTerms, 
            getActiveTermByType, getTermById, getAllTerms, 
            verifyTermAcceptance, verifyAllTermsAccepted, 
            loggingService
        );
    }
}
