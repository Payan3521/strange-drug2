package com.microserviceone.users.termsAndConditionsApi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AcceptedMultipleRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "User email is required")
    @Email(message = "Email must be valid")
    private String userEmail;

    @NotNull(message = "Term IDs are required")
    private List<Long> termIds;

    public AcceptedMultipleRequest() {}

    public AcceptedMultipleRequest(Long userId, String userEmail, List<Long> termIds) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.termIds = termIds;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<Long> getTermIds() { return termIds; }
    public void setTermIds(List<Long> termIds) { this.termIds = termIds; }
}