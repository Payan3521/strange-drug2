package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.domain.model.User;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity.AcceptedEntity;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AcceptedMapper {
    
    private final TermMapper termMapper;
    private final LoggingService loggingService;
    
    public Accepted toDomain(AcceptedEntity entity) {
        try {
            if (entity == null) {
                loggingService.logDebug("AcceptedMapper: Entidad nula recibida en toDomain");
                return null;
            }
            
            loggingService.logDebug("AcceptedMapper: Convirtiendo entidad a dominio - ID: {}, UserID: {}, TermID: {}", 
                entity.getId(), entity.getUserId(), entity.getTerminoAceptado().getId());
            
            User user = new User(entity.getUserId(), entity.getUserEmail());
            
            Accepted accepted = new Accepted(
                entity.getId(),
                user,
                entity.getFechaHoraDeAceptacion(),
                termMapper.toDomain(entity.getTerminoAceptado()),
                entity.getIp()
            );
            
            loggingService.logDebug("AcceptedMapper: Entidad convertida exitosamente a dominio - ID: {}, UserID: {}, TermID: {}, IP: {}", 
                accepted.getId(), accepted.getUser().getId(), accepted.getTerminoAceptado().getId(), accepted.getIp());
            
            return accepted;
            
        } catch (Exception e) {
            loggingService.logError("AcceptedMapper: Error al convertir entidad a dominio - EntityID: {}", 
                entity != null ? entity.getId() : "null", e);
            throw e;
        }
    }

    public AcceptedEntity toEntity(Accepted domain) {
        try {
            if (domain == null) {
                loggingService.logDebug("AcceptedMapper: Dominio nulo recibido en toEntity");
                return null;
            }
            
            loggingService.logDebug("AcceptedMapper: Convirtiendo dominio a entidad - ID: {}, UserID: {}, TermID: {}", 
                domain.getId(), domain.getUser().getId(), domain.getTerminoAceptado().getId());
            
            AcceptedEntity entity = new AcceptedEntity();
            entity.setId(domain.getId());
            entity.setUserId(domain.getUser().getId());
            entity.setUserEmail(domain.getUser().getEmail());
            entity.setFechaHoraDeAceptacion(domain.getFechaHoraDeAceptacion());
            entity.setTerminoAceptado(termMapper.toEntity(domain.getTerminoAceptado()));
            entity.setIp(domain.getIp());
            
            loggingService.logDebug("AcceptedMapper: Dominio convertido exitosamente a entidad - ID: {}, UserID: {}, TermID: {}, IP: {}", 
                entity.getId(), entity.getUserId(), entity.getTerminoAceptado().getId(), entity.getIp());
            
            return entity;
            
        } catch (Exception e) {
            loggingService.logError("AcceptedMapper: Error al convertir dominio a entidad - DomainID: {}", 
                domain != null ? domain.getId() : "null", e);
            throw e;
        }
    }
} 