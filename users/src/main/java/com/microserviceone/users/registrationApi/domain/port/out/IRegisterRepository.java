package com.microserviceone.users.registrationApi.domain.port.out;

import java.util.Optional;
import com.microserviceone.users.registrationApi.domain.model.User;

public interface IRegisterRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}