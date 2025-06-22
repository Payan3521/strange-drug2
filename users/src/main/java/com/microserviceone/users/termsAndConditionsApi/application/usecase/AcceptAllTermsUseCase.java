package com.microserviceone.users.termsAndConditionsApi.application.usecase;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.microserviceone.users.termsAndConditionsApi.application.exception.TermNotActiveOrNotFoundException;
import com.microserviceone.users.termsAndConditionsApi.application.exception.UserAlreadyAcceptedException;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptAllTerms;
import com.microserviceone.users.termsAndConditionsApi.domain.port.in.IAcceptTerm;

@Service
public class AcceptAllTermsUseCase implements IAcceptAllTerms{

    private final IAcceptTerm acceptTerm;

    public AcceptAllTermsUseCase(@Qualifier("AcceptTermUseCase") IAcceptTerm acceptTerm) {
        this.acceptTerm = acceptTerm;
    }

    @Override
    public List<Accepted> acceptAll(Long userId, String userEmail, List<Long> termIds, String ipAddress) {
        List<Accepted> accepteds = new ArrayList<>();
        
        for (Long termId : termIds) {
            try {
                // Intenta aceptar el término
                Accepted accepted = acceptTerm.acceptTerm(userId, userEmail, termId, ipAddress);
                accepteds.add(accepted);
            } catch (TermNotActiveOrNotFoundException e) {
                // Si el término está inactivo o no existe, lo ignora y continúa
            } catch (UserAlreadyAcceptedException e) {
                // Si el término ya fue aceptado, lo ignora y continúa
            }
        }
        return accepteds;
    }
    
}