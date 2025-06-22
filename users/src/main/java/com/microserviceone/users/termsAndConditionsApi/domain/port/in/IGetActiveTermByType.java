package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import java.util.Optional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;

public interface IGetActiveTermByType {
    Optional<TermAndCondition> getActiveTermByType(String type);
}