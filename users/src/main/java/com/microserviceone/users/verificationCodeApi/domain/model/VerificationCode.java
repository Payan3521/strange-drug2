package com.microserviceone.users.verificationCodeApi.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class VerificationCode {
    
    private static final int EXPIRATION_MINUTES = 5;
    
    private Long id;
    private String email;
    private String code;
    private LocalDateTime generatedAt;
    private boolean used;

    public VerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.generatedAt = LocalDateTime.now();
        this.used = false;
        
        validateEmail(email);
        validateCode(code);
    }
    
    public VerificationCode(Long id, String email, String code, LocalDateTime generatedAt, boolean used) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.code = Objects.requireNonNull(code, "Code cannot be null");
        this.generatedAt = Objects.requireNonNull(generatedAt, "Generated date cannot be null");
        this.used = used;
        
        validateEmail(email);
        validateCode(code);
    }


    public boolean isExpired() {
        return generatedAt.plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now());
    }


    public boolean isValid() {
        return !used && !isExpired();
    }

    public void markAsUsed() {
        if (used) {
            throw new IllegalStateException("Verification code has already been used");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot use expired verification code");
        }
        this.used = true;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateCode(String code) {
        if (code == null || !code.matches("\\d{6}")) {
            throw new IllegalArgumentException("Code must be exactly 6 digits");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationCode that = (VerificationCode) o;
        return Objects.equals(email, that.email) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, code);
    }

}