package com.microserviceone.users.registrationApi.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for all REST endpoints.
 * Provides consistent response format across the microservice.
 * 
 * @param <T> Type of the response data
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful response with data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    /**
     * Creates a successful response without data.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /**
     * Creates an error response with error code.
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    /**
     * Creates an error response without error code.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

}