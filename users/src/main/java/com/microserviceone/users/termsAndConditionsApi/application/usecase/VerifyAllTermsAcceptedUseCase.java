package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyAllTermsAccepted;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermsNotAcceptedException;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyAllTermsAcceptedUseCase implements IVerifyAllTermsAccepted{
    
    private final ITermAcceptanceRepository termAcceptanceRepository;
    private final ITermRepository termRepository;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public void verifyAllTermsAccepted(String userEmail) {
        try {
            loggingService.logInfo("VerifyAllTermsAcceptedUseCase: Verificando que todos los términos sean aceptados - Email: {}", userEmail);
            
            // Obtener todos los términos activos
            loggingService.logDebug("VerifyAllTermsAcceptedUseCase: Obteniendo todos los términos activos");
            List<TermAndCondition> activeTerms = termRepository.findAllActiveTerms();
            loggingService.logDebug("VerifyAllTermsAcceptedUseCase: Términos activos obtenidos - Cantidad: {}", activeTerms.size());
            
            // Obtener el ID del usuario por email
            loggingService.logDebug("VerifyAllTermsAcceptedUseCase: Obteniendo UserID por email: {}", userEmail);
            Long userId = termAcceptanceRepository.getUserIdByEmail(userEmail)
                .orElseThrow(() -> new TermsNotAcceptedException("Usuario no encontrado"));
            loggingService.logDebug("VerifyAllTermsAcceptedUseCase: UserID obtenido: {} para email: {}", userId, userEmail);
            
            // Verificar que el usuario ha aceptado todos los términos
            loggingService.logDebug("VerifyAllTermsAcceptedUseCase: Verificando aceptación de cada término para UserID: {}", userId);
            for (TermAndCondition term : activeTerms) {
                if (!termAcceptanceRepository.existsByUserIdAndTermId(userId, term.getId())) {
                    loggingService.logWarning("VerifyAllTermsAcceptedUseCase: Usuario no ha aceptado el término - UserID: {}, TermID: {}, Título: {}", 
                        userId, term.getId(), term.getTitle());
                    throw new TermsNotAcceptedException("Debes aceptar todos los términos y condiciones");
                }
                loggingService.logDebug("VerifyAllTermsAcceptedUseCase: Término verificado como aceptado - UserID: {}, TermID: {}", userId, term.getId());
            }
            
            loggingService.logInfo("VerifyAllTermsAcceptedUseCase: Verificación completada - Todos los términos aceptados para UserID: {} (Email: {})", 
                userId, userEmail);
            
        } catch (TermsNotAcceptedException e) {
            loggingService.logError("VerifyAllTermsAcceptedUseCase: Excepción de términos no aceptados - Email: {}", userEmail, e);
            throw e;
        } catch (Exception e) {
            loggingService.logError("VerifyAllTermsAcceptedUseCase: Error inesperado al verificar términos - Email: {}", userEmail, e);
            throw e;
        }
    }
} 