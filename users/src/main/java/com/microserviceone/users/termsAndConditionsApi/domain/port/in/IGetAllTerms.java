package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import java.util.List;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;

public interface IGetAllTerms {
    List<TermAndCondition> getAllTerms();
}