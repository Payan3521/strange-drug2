package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.model.User;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptTerm;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service("AcceptTermUseCase")
@RequiredArgsConstructor
public class AcceptTermUseCase implements IAcceptTerm {

    private final ValidateExistTermUseCase validateExistTermUseCase;
    private final ValidateAcceptedTermUseCase validateAcceptedTermUseCase;
    private final ITermAcceptanceRepository termAcceptanceRepository;
    private final GetTermUseCase getTermUseCase;
    private final LoggingService loggingService;

    @Override
    @Transactional
    public Accepted acceptTerm(Long userId, String userEmail, Long termId, String ipAddress) {
        try {
            loggingService.logInfo("AcceptTermUseCase: Iniciando aceptación de término - UserID: {}, Email: {}, TermID: {}, IP: {}", 
                userId, userEmail, termId, ipAddress);
            
            // Validar que el término existe y está activo
            loggingService.logDebug("AcceptTermUseCase: Validando existencia y estado del término - TermID: {}", termId);
            validateExistTermUseCase.validateTerm(termId);
            loggingService.logDebug("AcceptTermUseCase: Término validado como existente y activo - TermID: {}", termId);
            
            // Validar que el usuario no ha aceptado este término antes
            loggingService.logDebug("AcceptTermUseCase: Validando que el usuario no haya aceptado previamente - UserID: {}, TermID: {}", userId, termId);
            validateAcceptedTermUseCase.existsByUserIdAndTermId(userId, termId);
            loggingService.logDebug("AcceptTermUseCase: Usuario no ha aceptado previamente el término - UserID: {}, TermID: {}", userId, termId);
            
            // Obtener el término
            loggingService.logDebug("AcceptTermUseCase: Obteniendo información del término - TermID: {}", termId);
            TermAndCondition term = getTermUseCase.getTerm(termId);
            loggingService.logDebug("AcceptTermUseCase: Término obtenido - TermID: {}, Título: {}", termId, term.getTitle());
                
            // Crear el usuario
            loggingService.logDebug("AcceptTermUseCase: Creando objeto usuario - UserID: {}, Email: {}", userId, userEmail);
            User user = new User(userId, userEmail);
            
            // Crear la aceptación con todos los datos requeridos
            loggingService.logDebug("AcceptTermUseCase: Creando objeto de aceptación - UserID: {}, TermID: {}, IP: {}", userId, termId, ipAddress);
            Accepted accepted = new Accepted(user, term, ipAddress);
            
            // Guardar y retornar la aceptación
            loggingService.logDebug("AcceptTermUseCase: Guardando aceptación en base de datos");
            Accepted savedAccepted = termAcceptanceRepository.save(accepted);
            
            loggingService.logInfo("AcceptTermUseCase: Término aceptado exitosamente - UserID: {}, TermID: {}, AcceptanceID: {}", 
                userId, termId, savedAccepted.getId());
            
            return savedAccepted;
            
        } catch (Exception e) {
            loggingService.logError("AcceptTermUseCase: Error al aceptar término - UserID: {}, Email: {}, TermID: {}", 
                userId, userEmail, termId, e);
            throw e;
        }
    }
}