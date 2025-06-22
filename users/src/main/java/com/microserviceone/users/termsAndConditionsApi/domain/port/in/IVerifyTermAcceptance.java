package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

public interface IVerifyTermAcceptance {
    boolean hasAcceptedTerm(Long userId, Long termId);
}