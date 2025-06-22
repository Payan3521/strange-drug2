package com.microserviceone.users.termsAndConditionsApi.domain.port.in;

public interface IVerifyAllTermsAccepted {
    void verifyAllTermsAccepted(String userEmail);
}