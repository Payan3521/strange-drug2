package com.microserviceone.users.registrationApi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{6,}@gmail\\.com$", message = "Email must be a valid Gmail address with at least 6 characters before @gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character")
    private String password;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^3[0-9]{9}$", message = "Phone must be a valid Colombian mobile number (10 digits starting with 3)")
    private String phone;

    @NotBlank(message = "Area is required")
    @Size(min = 2, max = 100, message = "Area must be between 2 and 100 characters")
    private String area;
}
