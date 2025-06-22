package com.microserviceone.users.verificationCodeApi.domain.port.in;

public interface ICheckEmailVerification {
    void checkEmailVerification(String email);
}