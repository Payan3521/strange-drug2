package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.mapper;

import org.springframework.stereotype.Component;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity.TermAndConditionEntity;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TermMapper {
    
    private final LoggingService loggingService;
    
    public TermAndCondition toDomain(TermAndConditionEntity entity) {
        try {
            if (entity == null) {
                loggingService.logDebug("TermMapper: Entidad nula recibida en toDomain");
                return null;
            }
            
            loggingService.logDebug("TermMapper: Convirtiendo entidad a dominio - ID: {}, Título: {}, Tipo: {}", 
                entity.getId(), entity.getTitle(), entity.getType());
            
            TermAndCondition term = new TermAndCondition(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVersion(),
                entity.getCreateTerm(),
                entity.isActive(),
                TermAndCondition.TermType.valueOf(entity.getType())
            );
            
            loggingService.logDebug("TermMapper: Entidad convertida exitosamente a dominio - ID: {}, Título: {}, Tipo: {}, Activo: {}", 
                term.getId(), term.getTitle(), term.getType(), term.isActive());
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("TermMapper: Error al convertir entidad a dominio - EntityID: {}", 
                entity != null ? entity.getId() : "null", e);
            throw e;
        }
    }

    public TermAndConditionEntity toEntity(TermAndCondition domain) {
        try {
            if (domain == null) {
                loggingService.logDebug("TermMapper: Dominio nulo recibido en toEntity");
                return null;
            }
            
            loggingService.logDebug("TermMapper: Convirtiendo dominio a entidad - ID: {}, Título: {}, Tipo: {}", 
                domain.getId(), domain.getTitle(), domain.getType());
            
            TermAndConditionEntity entity = new TermAndConditionEntity();
            entity.setId(domain.getId());
            entity.setTitle(domain.getTitle());
            entity.setContent(domain.getContent());
            entity.setVersion(domain.getVersion());
            entity.setCreateTerm(domain.getCreateTerm());
            entity.setActive(domain.isActive());
            entity.setType(domain.getType().name());
            
            loggingService.logDebug("TermMapper: Dominio convertido exitosamente a entidad - ID: {}, Título: {}, Tipo: {}, Activo: {}", 
                entity.getId(), entity.getTitle(), entity.getType(), entity.isActive());
            
            return entity;
            
        } catch (Exception e) {
            loggingService.logError("TermMapper: Error al convertir dominio a entidad - DomainID: {}", 
                domain != null ? domain.getId() : "null", e);
            throw e;
        }
    }
} 