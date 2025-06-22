package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermAcceptanceRepository;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.mapper.AcceptedMapper;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.repository.ORMaccepted;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity.AcceptedEntity;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor
public class AdapterAccepted implements ITermAcceptanceRepository {

    private final ORMaccepted ormAccepted;
    private final AcceptedMapper acceptedMapper;
    private final LoggingService loggingService;

    @Override
    public Accepted save(Accepted acceptance) {
        try {
            loggingService.logDebug("AdapterAccepted: Guardando aceptación en base de datos - UserID: {}, TermID: {}, IP: {}", 
                acceptance.getUser().getId(), acceptance.getTerminoAceptado().getId(), acceptance.getIp());
            
            AcceptedEntity acceptedEntity = acceptedMapper.toEntity(acceptance);
            loggingService.logDebug("AdapterAccepted: Aceptación convertida a entidad - UserID: {}, TermID: {}", 
                acceptance.getUser().getId(), acceptance.getTerminoAceptado().getId());
            
            AcceptedEntity savedAcceptedEntity = ormAccepted.save(acceptedEntity);
            loggingService.logDebug("AdapterAccepted: Aceptación guardada en base de datos - ID: {}, UserID: {}, TermID: {}", 
                savedAcceptedEntity.getId(), savedAcceptedEntity.getUserId(), savedAcceptedEntity.getTerminoAceptado().getId());
            
            Accepted savedAccepted = acceptedMapper.toDomain(savedAcceptedEntity);
            loggingService.logDebug("AdapterAccepted: Aceptación convertida de entidad a dominio - ID: {}, UserID: {}, TermID: {}", 
                savedAccepted.getId(), savedAccepted.getUser().getId(), savedAccepted.getTerminoAceptado().getId());
            
            return savedAccepted;
            
        } catch (Exception e) {
            loggingService.logError("AdapterAccepted: Error al guardar aceptación - UserID: {}, TermID: {}", 
                acceptance.getUser().getId(), acceptance.getTerminoAceptado().getId(), e);
            throw e;
        }
    }

    @Override
    public boolean existsByUserIdAndTermId(Long userId, Long termId) {
        try {
            loggingService.logDebug("AdapterAccepted: Verificando existencia de aceptación - UserID: {}, TermID: {}", userId, termId);
            
            boolean exists = ormAccepted.existsByUserIdAndTerminoAceptadoId(userId, termId);
            
            loggingService.logDebug("AdapterAccepted: Aceptación {} existe - UserID: {}, TermID: {}", 
                exists ? "SÍ" : "NO", userId, termId);
            
            return exists;
            
        } catch (Exception e) {
            loggingService.logError("AdapterAccepted: Error al verificar existencia de aceptación - UserID: {}, TermID: {}", userId, termId, e);
            throw e;
        }
    }

    @Override
    public Optional<Long> getUserIdByEmail(String email) {
        try {
            loggingService.logDebug("AdapterAccepted: Buscando UserID por email: {}", email);
            
            Optional<Long> userId = ormAccepted.findFirstByUserEmail(email);
            
            if (userId.isPresent()) {
                loggingService.logDebug("AdapterAccepted: UserID encontrado: {} para email: {}", userId.get(), email);
            } else {
                loggingService.logDebug("AdapterAccepted: UserID no encontrado para email: {}", email);
            }
            
            return userId;
            
        } catch (Exception e) {
            loggingService.logError("AdapterAccepted: Error al buscar UserID por email: {}", email, e);
            throw e;
        }
    }
}