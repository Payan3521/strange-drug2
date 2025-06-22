package com.microserviceone.users.registrationApi.domain.port.in;

import com.microserviceone.users.registrationApi.domain.model.Customer;

public interface ISaveCustomer {
    Customer save(Customer customer);
}