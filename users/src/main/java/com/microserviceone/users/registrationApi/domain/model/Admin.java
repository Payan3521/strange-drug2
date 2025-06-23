package com.microserviceone.users.registrationApi.domain.model;

import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) 
public class Admin extends User{
    private String area;

    public Admin(String name, String lastName, String email, String password, String phone, String area) {
        super(name, lastName, email, password, phone, UserRole.ADMIN);
        this.area = Objects.requireNonNull(area, "Area cannot be null");
    }

    public Admin(Long id, String name, String lastName, String email, String password, 
                String phone, String area, boolean verifiedCode, boolean verifiedTerm) {
        super(id, name, lastName, email, password, phone,UserRole.ADMIN, verifiedCode, verifiedTerm);
        this.area = Objects.requireNonNull(area, "Area cannot be null");
    }

    @Override
    protected void validateSpecificData() {
        validateArea(area);
    }

    private void validateArea(String area) {
        if (area == null || area.trim().isEmpty()) {
            throw new IllegalArgumentException("Area cannot be null or empty");
        }
        if (area.length() < 2 || area.length() > 100) {
            throw new IllegalArgumentException("Area must be between 2 and 100 characters");
        }
    }

}