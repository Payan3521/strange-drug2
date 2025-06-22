package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermNotActiveOrNotFoundException;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateExistTermUseCase {
    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Transactional(readOnly = true)
    public void validateTerm(Long termId){
        try {
            loggingService.logDebug("ValidateExistTermUseCase: Validando existencia y estado del término - TermID: {}", termId);
            
            termRepository.findById(termId)
                .filter(term -> term.isActive())
                .orElseThrow(() -> new TermNotActiveOrNotFoundException());
            
            loggingService.logDebug("ValidateExistTermUseCase: Término validado como existente y activo - TermID: {}", termId);
            
        } catch (TermNotActiveOrNotFoundException e) {
            loggingService.logWarning("ValidateExistTermUseCase: Término no encontrado o inactivo - TermID: {}", termId);
            throw e;
        } catch (Exception e) {
            loggingService.logError("ValidateExistTermUseCase: Error inesperado al validar término - TermID: {}", termId, e);
            throw e;
        }
    }
}