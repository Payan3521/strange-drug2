package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.application.exception.UserAlreadyAcceptedException;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateAcceptedTermUseCase {

    private final ITermAcceptanceRepository termAcceptanceRepository;
    private final LoggingService loggingService;

    @Transactional(readOnly = true)
    public void existsByUserIdAndTermId(Long userId, Long termId){
        try {
            loggingService.logDebug("ValidateAcceptedTermUseCase: Validando si usuario ya aceptó el término - UserID: {}, TermID: {}", userId, termId);
            
            if(termAcceptanceRepository.existsByUserIdAndTermId(userId, termId)){
                loggingService.logWarning("ValidateAcceptedTermUseCase: Usuario ya aceptó el término previamente - UserID: {}, TermID: {}", userId, termId);
                throw new UserAlreadyAcceptedException();
            }
            
            loggingService.logDebug("ValidateAcceptedTermUseCase: Usuario no ha aceptado el término previamente - UserID: {}, TermID: {}", userId, termId);
            
        } catch (UserAlreadyAcceptedException e) {
            loggingService.logError("ValidateAcceptedTermUseCase: Excepción de término ya aceptado - UserID: {}, TermID: {}", userId, termId, e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("ValidateAcceptedTermUseCase: Error inesperado al validar aceptación - UserID: {}, TermID: {}", userId, termId, e);
            throw e;
        }
    }
}