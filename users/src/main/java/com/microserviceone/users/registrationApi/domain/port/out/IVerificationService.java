package com.microserviceone.users.registrationApi.domain.port.out;

public interface IVerificationService {
    boolean isEmailVerified(String email, String code);
}