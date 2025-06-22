package com.microserviceone.users.termsAndConditionsApi.application.service;

import java.util.List;
import java.util.Optional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptTerm;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetActiveTermByType;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllActiveTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IGetTermById;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyAllTermsAccepted;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IVerifyTermAcceptance;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TermsService implements IAcceptTerm, IAcceptAllTerms, IGetAllActiveTerms, IGetActiveTermByType, IGetTermById, IGetAllTerms, IVerifyTermAcceptance, IVerifyAllTermsAccepted{

    private final IAcceptTerm acceptTerm;
    private final IAcceptAllTerms acceptAllTerms;
    private final IGetAllActiveTerms getAllActiveTerms;
    private final IGetActiveTermByType getActiveTermByType;
    private final IGetTermById getTermById;
    private final IGetAllTerms getAllTerms;
    private final IVerifyTermAcceptance verifyTermAcceptance;
    private final IVerifyAllTermsAccepted verifyAllTermsAccepted;
    private final LoggingService loggingService;

    @Override
    public boolean hasAcceptedTerm(Long userId, Long termId) {
        try {
            loggingService.logDebug("TermsService: Verificando aceptación de término - UserID: {}, TermID: {}", userId, termId);
            
            boolean hasAccepted = verifyTermAcceptance.hasAcceptedTerm(userId, termId);
            
            loggingService.logDebug("TermsService: Verificación completada - UserID: {}, TermID: {}, Aceptado: {}", 
                userId, termId, hasAccepted);
            
            return hasAccepted;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al verificar aceptación de término - UserID: {}, TermID: {}", 
                userId, termId, e);
            throw e;
        }
    }

    @Override
    public List<TermAndCondition> getAllActiveTerms() {
        try {
            loggingService.logInfo("TermsService: Obteniendo todos los términos activos");
            
            List<TermAndCondition> activeTerms = getAllActiveTerms.getAllActiveTerms();
            
            loggingService.logInfo("TermsService: Términos activos obtenidos exitosamente - Cantidad: {}", activeTerms.size());
            
            return activeTerms;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al obtener términos activos", e);
            throw e;
        }
    }

    @Override
    public Optional<TermAndCondition> getActiveTermByType(String type) {
        try {
            loggingService.logDebug("TermsService: Obteniendo término activo por tipo: {}", type);
            
            Optional<TermAndCondition> term = getActiveTermByType.getActiveTermByType(type);
            
            if (term.isPresent()) {
                loggingService.logDebug("TermsService: Término encontrado - Tipo: {}, ID: {}, Título: {}", 
                    type, term.get().getId(), term.get().getTitle());
            } else {
                loggingService.logDebug("TermsService: No se encontró término activo para el tipo: {}", type);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al obtener término por tipo: {}", type, e);
            throw e;
        }
    }

    @Override
    public Accepted acceptTerm(Long userId, String userEmail, Long termId, String ipAddress) {
        try {
            loggingService.logInfo("TermsService: Procesando aceptación de término - UserID: {}, Email: {}, TermID: {}, IP: {}", 
                userId, userEmail, termId, ipAddress);
            
            Accepted accepted = acceptTerm.acceptTerm(userId, userEmail, termId, ipAddress);
            
            loggingService.logInfo("TermsService: Término aceptado exitosamente - UserID: {}, TermID: {}, AcceptanceID: {}", 
                userId, termId, accepted.getId());
            
            return accepted;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al aceptar término - UserID: {}, Email: {}, TermID: {}", 
                userId, userEmail, termId, e);
            throw e;
        }
    }

    @Override
    public void verifyAllTermsAccepted(String userEmail) {
        try {
            loggingService.logInfo("TermsService: Verificando que todos los términos sean aceptados - Email: {}", userEmail);
            
            verifyAllTermsAccepted.verifyAllTermsAccepted(userEmail);
            
            loggingService.logInfo("TermsService: Verificación completada - Todos los términos aceptados para email: {}", userEmail);
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al verificar todos los términos - Email: {}", userEmail, e);
            throw e;
        }
    }

    @Override
    public List<TermAndCondition> getAllTerms() {
        try {
            loggingService.logInfo("TermsService: Obteniendo todos los términos (activos e inactivos)");
            
            List<TermAndCondition> allTerms = getAllTerms.getAllTerms();
            
            loggingService.logInfo("TermsService: Todos los términos obtenidos exitosamente - Cantidad: {}", allTerms.size());
            
            return allTerms;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al obtener todos los términos", e);
            throw e;
        }
    }

    @Override
    public Optional<TermAndCondition> getById(Long id) {
        try {
            loggingService.logDebug("TermsService: Obteniendo término por ID: {}", id);
            
            Optional<TermAndCondition> term = getTermById.getById(id);
            
            if (term.isPresent()) {
                loggingService.logDebug("TermsService: Término encontrado - ID: {}, Título: {}, Tipo: {}", 
                    id, term.get().getTitle(), term.get().getType());
            } else {
                loggingService.logDebug("TermsService: No se encontró término con ID: {}", id);
            }
            
            return term;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al obtener término por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Accepted> acceptAll(Long userId, String userEmail, List<Long> termIds, String ipAddress) {
        try {
            loggingService.logInfo("TermsService: Procesando aceptación múltiple de términos - UserID: {}, Email: {}, Cantidad: {}, IP: {}", 
                userId, userEmail, termIds.size(), ipAddress);
            
            List<Accepted> acceptedList = acceptAllTerms.acceptAll(userId, userEmail, termIds, ipAddress);
            
            loggingService.logInfo("TermsService: Aceptación múltiple completada - UserID: {}, Términos aceptados: {}, Cantidad: {}", 
                userId, acceptedList.size(), acceptedList.size());
            
            return acceptedList;
            
        } catch (Exception e) {
            loggingService.logError("TermsService: Error al aceptar múltiples términos - UserID: {}, Email: {}, Cantidad: {}", 
                userId, userEmail, termIds.size(), e);
            throw e;
        }
    }
    
}