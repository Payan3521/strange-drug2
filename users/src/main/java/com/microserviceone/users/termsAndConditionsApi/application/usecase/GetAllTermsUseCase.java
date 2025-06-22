package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllTermsUseCase implements IGetAllTerms{

    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public List<TermAndCondition> getAllTerms() {
        try {
            loggingService.logInfo("GetAllTermsUseCase: Obteniendo todos los términos (activos e inactivos)");
            
            List<TermAndCondition> allTerms = termRepository.findAll();
            
            loggingService.logInfo("GetAllTermsUseCase: Todos los términos obtenidos exitosamente - Cantidad: {}", allTerms.size());
            
            return allTerms;
            
        } catch (Exception e) {
            loggingService.logError("GetAllTermsUseCase: Error al obtener todos los términos", e);
            throw e;
        }
    }

}