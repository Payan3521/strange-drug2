package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermNotActiveOrNotFoundException;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTermUseCase {
    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Transactional(readOnly = true)
    public TermAndCondition getTerm(Long termId){
        try {
            loggingService.logDebug("GetTermUseCase: Buscando término por ID: {}", termId);
            
            TermAndCondition term = termRepository.findById(termId)
                .orElseThrow(TermNotActiveOrNotFoundException::new);
            
            loggingService.logDebug("GetTermUseCase: Término encontrado - ID: {}, Título: {}, Tipo: {}, Activo: {}", 
                termId, term.getTitle(), term.getType(), term.isActive());
            
            return term;
            
        } catch (TermNotActiveOrNotFoundException e) {
            loggingService.logWarning("GetTermUseCase: Término no encontrado o inactivo - TermID: {}", termId);
            throw e;
        } catch (Exception e) {
            loggingService.logError("GetTermUseCase: Error al obtener término - TermID: {}", termId, e);
            throw e;
        }
    } 
}