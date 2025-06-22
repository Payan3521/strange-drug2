package com.microserviceone.users.verificationCodeApi.domain.port.out;

public interface IEmailService {
    void sendVerificationCode(String email, String code);
}