package com.microserviceone.users.verificationCodeApi.infraestructure.persistance.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "verification_codes")
public class VerificationCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    // Constructors
    public VerificationCodeEntity() {}

    public VerificationCodeEntity(String email, String code, LocalDateTime generatedAt, boolean used, boolean verified) {
        this.email = email;
        this.code = code;
        this.generatedAt = generatedAt;
        this.used = used;
        this.verified = verified;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}