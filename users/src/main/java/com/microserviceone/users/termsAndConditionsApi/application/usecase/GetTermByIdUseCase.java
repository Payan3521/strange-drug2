package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetTermById;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("getTermByIdUseCase")
@RequiredArgsConstructor
public class GetTermByIdUseCase implements IGetTermById{

    private final GetTermUseCase getTermUseCase;
    private final LoggingService loggingService;

    @Override
    @Transactional(readOnly = true)
    public Optional<TermAndCondition> getById(Long id) {
        try {
            loggingService.logDebug("GetTermByIdUseCase: Obteniendo término por ID: {}", id);
            
            Optional<TermAndCondition> term = Optional.of(getTermUseCase.getTerm(id));
            
            if (term.isPresent()) {
                loggingService.logDebug("GetTermByIdUseCase: Término encontrado - ID: {}, Título: {}, Tipo: {}", 
                    id, term.get().getTitle(), term.get().getType());
            } else {
                loggingService.logDebug("GetTermByIdUseCase: No se encontró término con ID: {}", id);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("GetTermByIdUseCase: Error al obtener término por ID: {}", id, e);
            throw e;
        }
    }

}