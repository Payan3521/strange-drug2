package com.microserviceone.users.registrationApi.web.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String rol;
    private LocalDate birthDate; // Only for customers
    private String area; // Only for admins
}