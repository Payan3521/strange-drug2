package com.microserviceone.users.registrationApi.application.exception;

public class PasswordNullOrEmptyException extends RuntimeException{
    public PasswordNullOrEmptyException() {
        super("Password cannot be null or empty");
    }
}