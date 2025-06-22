package com.microserviceone.users.verificationCodeApi.domain.port.in;

public interface IVerifyCode {
    boolean verifyCode(String email, String code);
}