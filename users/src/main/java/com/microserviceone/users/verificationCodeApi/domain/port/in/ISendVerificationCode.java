package com.microserviceone.users.verificationCodeApi.domain.port.in;

public interface ISendVerificationCode {
    void sendCode(String email);
}