package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;

public interface IAcceptTerm {
    Accepted acceptTerm(Long userId, String userEmail, Long termId, String ipAddress);
} 