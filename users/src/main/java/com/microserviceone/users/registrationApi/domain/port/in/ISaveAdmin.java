package com.microserviceone.users.registrationApi.domain.port.in;

import com.microserviceone.users.registrationApi.domain.model.Admin;

public interface ISaveAdmin {
    Admin save(Admin admin);
}