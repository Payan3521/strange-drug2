package com.microserviceone.users.termsAndConditionsApi.web.webMapper;

import org.springframework.stereotype.Component;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;
import com.microserviceone.users.termsAndConditionsApi.web.dto.TermResponse;

@Component
public class TermWebMapper {
    
    public TermResponse toResponse(TermAndCondition term) {
        if (term == null) return null;
        
        return new TermResponse(
            term.getId(),
            term.getTitle(),
            term.getContent(),
            term.getVersion(),
            term.getCreateTerm(),
            term.isActive(),
            term.getType().name()
        );
    }
} 