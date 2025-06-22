package com.microserviceone.users.verificationCodeApi.domain.port.out;

import java.util.Optional;
import com.microserviceone.users.verificationCodeApi.domain.model.VerificationCode;

public interface IVerificationCodeRepository {
    VerificationCode save(VerificationCode verificationCode);
    Optional<VerificationCode> findByEmailAndCode(String email, String code);
    void deleteExpiredCodes();
    void invalidatePreviousCodes(String email);
    boolean isEmailVerified(String email);
    void markCodeAsVerified(String email, String code);
}