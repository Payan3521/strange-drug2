package com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.microserviceone.users.termsAndConditionsApi.infraestructure.persistance.entity.TermAndConditionEntity;

public interface ORMterm  extends JpaRepository<TermAndConditionEntity, Long> {
    
    @Query("SELECT t FROM TermAndConditionEntity t WHERE t.active = true ORDER BY t.type, t.createTerm DESC")
    List<TermAndConditionEntity> findAllActiveTerms();
    
    @Query("SELECT t FROM TermAndConditionEntity t WHERE t.active = true AND t.type = ?1 ORDER BY t.createTerm DESC")
    Optional<TermAndConditionEntity> findActiveTermByType(String type);
} 