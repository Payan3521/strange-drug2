package com.microserviceone.users.registrationApi.domain.port.out;

public interface ITermsService {
    boolean hasAcceptedTerms(Long userId, String email);
    void acceptTerms(Long userId, String email, Long termId, String ip);
}