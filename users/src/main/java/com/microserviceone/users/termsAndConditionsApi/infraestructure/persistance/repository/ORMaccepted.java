package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity.AcceptedEntity;
import java.util.Optional;

public interface ORMaccepted extends JpaRepository<AcceptedEntity, Long> {
    boolean existsByUserIdAndTerminoAceptadoId(Long userId, Long terminoAceptadoId);
    
    @Query("SELECT a.userId FROM AcceptedEntity a WHERE a.userEmail = :userEmail")
    Optional<Long> findFirstByUserEmail(String userEmail);
} 