package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import java.util.List;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;

public interface IGetAllActiveTerms {
    List<TermAndCondition> getAllActiveTerms();
} 