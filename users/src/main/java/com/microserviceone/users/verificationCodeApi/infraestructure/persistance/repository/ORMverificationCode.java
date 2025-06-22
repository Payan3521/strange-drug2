package com.microserviceone.users.verificationCodeApi.infraestructure.persistance.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.microserviceone.users.verificationCodeApi.infraestructure.persistance.entity.VerificationCodeEntity;

@Repository
public interface ORMverificationCode extends JpaRepository<VerificationCodeEntity, Long> {
    
    Optional<VerificationCodeEntity> findByEmailAndCode(String email, String code);
    
    @Modifying
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.generatedAt < :expiredTime")
    void deleteByGeneratedAtBefore(LocalDateTime expiredTime);

    @Modifying
    @Query("UPDATE VerificationCodeEntity v SET v.used = true WHERE v.email = :email AND v.used = false")
    void invalidatePreviousCodes(String email);

    boolean existsByEmailAndVerifiedTrue(String email);

    @Modifying
    @Query("UPDATE VerificationCodeEntity v SET v.verified = true WHERE v.email = :email AND v.code = :code")
    void markAsVerified(String email, String code);
}