package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyTermAcceptance;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("verifyTermAcceptanceUseCase")
@RequiredArgsConstructor
public class VerifyTermAcceptanceUseCase implements IVerifyTermAcceptance{

    private final ITermAcceptanceRepository termAcceptanceRepository;
    private final LoggingService loggingService;
    private final ValidateExistTermUseCase validateExistTermUseCase;

    @Override
    @Transactional(readOnly = true)
    public boolean hasAcceptedTerm(Long userId, Long termId) {
        try {

            loggingService.logDebug("Validando que el termino exista", termId);

            validateExistTermUseCase.validateTerm(termId);

            loggingService.logDebug("VerifyTermAcceptanceUseCase: Verificando aceptación de término - UserID: {}, TermID: {}", userId, termId);
            
            boolean hasAccepted = termAcceptanceRepository.existsByUserIdAndTermId(userId, termId);
            
            loggingService.logDebug("VerifyTermAcceptanceUseCase: Verificación completada - UserID: {}, TermID: {}, Aceptado: {}", 
                userId, termId, hasAccepted);
            
            return hasAccepted;
            
        } catch (Exception e) {
            loggingService.logError("VerifyTermAcceptanceUseCase: Error al verificar aceptación de término - UserID: {}, TermID: {}", 
                userId, termId, e);
            throw e;
        }
    }

}