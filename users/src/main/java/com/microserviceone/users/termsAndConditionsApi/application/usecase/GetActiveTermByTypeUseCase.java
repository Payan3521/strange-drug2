package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetActiveTermByType;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("getActiveTermByTypeUseCase")
@RequiredArgsConstructor
public class GetActiveTermByTypeUseCase implements IGetActiveTermByType{

    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public Optional<TermAndCondition> getActiveTermByType(String type) {
        try {
            loggingService.logDebug("GetActiveTermByTypeUseCase: Buscando término activo por tipo: {}", type);
            
            Optional<TermAndCondition> term = termRepository.findActiveTermByType(type);
            
            if (term.isPresent()) {
                loggingService.logDebug("GetActiveTermByTypeUseCase: Término encontrado - Tipo: {}, ID: {}, Título: {}", 
                    type, term.get().getId(), term.get().getTitle());
            } else {
                loggingService.logDebug("GetActiveTermByTypeUseCase: No se encontró término activo para el tipo: {}", type);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("GetActiveTermByTypeUseCase: Error al buscar término por tipo: {}", type, e);
            throw e;
        }
    }
}