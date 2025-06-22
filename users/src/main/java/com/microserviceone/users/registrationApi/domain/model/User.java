package com.microserviceone.users.registrationApi.domain.model;

import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Setter 
@Getter
@NoArgsConstructor
public abstract class User {
    protected Long id;
    protected String name;
    protected String lastName;
    protected String email;
    protected String password;
    protected String phone;
    protected UserRole rol;
    

    protected User(String name, String lastName, String email, String password, String phone, UserRole rol){
        this(null, name, lastName, email, password, phone, rol);
    }

    protected User(Long id, String name, String lastName, String email, String password, 
                  String phone, UserRole rol){
        this.id= id;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.phone = Objects.requireNonNull(phone, "Phone cannot be null");
        this.rol = Objects.requireNonNull(rol, "Role cannot be null");

        validateUserData();
    }

    private void validateUserData() {
        validateName(name);
        validateLastName(lastName);
        validateEmail(email);
        validatePassword(password);
        validatePhone(phone);
        validateSpecificData();
    }

    protected void validateSpecificData() {
        // Override in subclasses if needed
    }

    public boolean isAdmin() {
        return rol == UserRole.ADMIN;
    }

    public boolean isCustomer() {
        return rol == UserRole.CUSTOMER;
    }


    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 2 and 50 characters");
        }
        if (!name.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Name can only contain letters and spaces");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (lastName.length() < 2 || lastName.length() > 50) {
            throw new IllegalArgumentException("Last name must be between 2 and 50 characters");
        }
        if (!lastName.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Last name can only contain letters and spaces");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        // In production, add more sophisticated password validation
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    public enum UserRole {
        CUSTOMER("Customer"),
        ADMIN("Administrator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}