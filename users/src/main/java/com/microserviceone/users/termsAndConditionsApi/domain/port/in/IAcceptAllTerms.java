package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import java.util.List;
import com.microserviceone.users.termsAndConditionsApi.domain.model.Accepted;

public interface IAcceptAllTerms {
    List<Accepted> acceptAll(Long userId, String userEmail, List<Long> termIds, String ipAddress);
}