package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.out.ITermRepository;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.mapper.TermMapper;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.repository.ORMterm;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdapterTerm implements ITermRepository {

    private final ORMterm ormTerm;
    private final TermMapper termMapper;
    private final LoggingService loggingService;

    @Override
    public List<TermAndCondition> findAllActiveTerms() {
        try {
            loggingService.logInfo("AdapterTerm: Obteniendo todos los términos activos");
            
            List<TermAndCondition> activeTerms = ormTerm.findAllActiveTerms().stream()
                .map(termMapper::toDomain)
                .collect(Collectors.toList());
            
            loggingService.logInfo("AdapterTerm: Términos activos obtenidos exitosamente - Cantidad: {}", activeTerms.size());
            
            return activeTerms;
            
        } catch (Exception e) {
            loggingService.logError("AdapterTerm: Error al obtener términos activos", e);
            throw e;
        }
    }

    @Override
    public Optional<TermAndCondition> findActiveTermByType(String type) {
        try {
            loggingService.logDebug("AdapterTerm: Buscando término activo por tipo: {}", type);
            
            Optional<TermAndCondition> term = ormTerm.findActiveTermByType(type)
                .map(termMapper::toDomain);
            
            if (term.isPresent()) {
                loggingService.logDebug("AdapterTerm: Término activo encontrado - Tipo: {}, ID: {}, Título: {}", 
                    type, term.get().getId(), term.get().getTitle());
            } else {
                loggingService.logDebug("AdapterTerm: No se encontró término activo para el tipo: {}", type);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("AdapterTerm: Error al buscar término activo por tipo: {}", type, e);
            throw e;
        }
    }

    @Override
    public Optional<TermAndCondition> findById(Long id) {
        try {
            loggingService.logDebug("AdapterTerm: Buscando término por ID: {}", id);
            
            Optional<TermAndCondition> term = ormTerm.findById(id)
                .map(termMapper::toDomain);
            
            if (term.isPresent()) {
                loggingService.logDebug("AdapterTerm: Término encontrado - ID: {}, Título: {}, Tipo: {}, Activo: {}", 
                    id, term.get().getTitle(), term.get().getType(), term.get().isActive());
            } else {
                loggingService.logDebug("AdapterTerm: No se encontró término con ID: {}", id);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("AdapterTerm: Error al buscar término por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<TermAndCondition> findAll() {
        try {
            loggingService.logInfo("AdapterTerm: Obteniendo todos los términos (activos e inactivos)");
            
            List<TermAndCondition> allTerms = ormTerm.findAll().stream()
                .map(termMapper::toDomain)
                .collect(Collectors.toList());
            
            loggingService.logInfo("AdapterTerm: Todos los términos obtenidos exitosamente - Cantidad: {}", allTerms.size());
            
            return allTerms;
            
        } catch (Exception e) {
            loggingService.logError("AdapterTerm: Error al obtener todos los términos", e);
            throw e;
        }
    }
}