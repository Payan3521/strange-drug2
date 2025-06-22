package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllActiveTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("getAllActiveTermsUseCase")
@RequiredArgsConstructor
public class GetAllActiveTermsUseCase implements IGetAllActiveTerms{

    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public List<TermAndCondition> getAllActiveTerms() {
        try {
            loggingService.logInfo("GetAllActiveTermsUseCase: Obteniendo todos los términos activos");
            
            List<TermAndCondition> activeTerms = termRepository.findAllActiveTerms();
            
            loggingService.logInfo("GetAllActiveTermsUseCase: Términos activos obtenidos exitosamente - Cantidad: {}", activeTerms.size());
            
            return activeTerms;
            
        } catch (Exception e) {
            loggingService.logError("GetAllActiveTermsUseCase: Error al obtener términos activos", e);
            throw e;
        }
    }

}