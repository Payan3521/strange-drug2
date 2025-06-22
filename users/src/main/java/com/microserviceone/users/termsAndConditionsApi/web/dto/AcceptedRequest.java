package com.microserviceone.users.termsAndConditionsApi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AcceptedRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Email must be valid")
    private String userEmail;

    @NotNull(message = "Term ID is required")
    private Long termId;

    public AcceptedRequest() {}

    public AcceptedRequest(Long userId, String userEmail, Long termId) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.termId = termId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
}  