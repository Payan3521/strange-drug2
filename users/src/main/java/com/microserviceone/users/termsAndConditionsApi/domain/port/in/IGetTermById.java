package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

import java.util.Optional;
import com.microserviceone.users.termsAndConditionsApi.domain.model.TermAndCondition;

public interface IGetTermById {
    Optional<TermAndCondition> getById(Long id);
}