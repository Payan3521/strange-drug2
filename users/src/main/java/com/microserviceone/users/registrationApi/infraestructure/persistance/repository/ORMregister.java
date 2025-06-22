package com.microserviceone.users.registrationApi.infraestructure.persistance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;

@Repository
public interface ORMregister extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}