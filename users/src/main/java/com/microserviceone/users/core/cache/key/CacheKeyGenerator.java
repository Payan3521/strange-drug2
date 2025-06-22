package com.microserviceone.users.core.cache.key;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Utility class for generating consistent cache keys
 * Provides standardized key generation for different cache scenarios
 */
@Component
public class CacheKeyGenerator {

    private static final String SEPARATOR = ":";
    private static final String NULL_VALUE = "null";

    /**
     * Generates a cache key for user by ID
     */
    public String generateUserByIdKey(Long userId) {
        return "user" + SEPARATOR + Objects.toString(userId, NULL_VALUE);
    }

    /**
     * Generates a cache key for user search with filters
     */
    public String generateUserSearchKey(String name, String lastName, String rol) {
        return "search" + SEPARATOR + 
               Objects.toString(name, NULL_VALUE) + SEPARATOR +
               Objects.toString(lastName, NULL_VALUE) + SEPARATOR +
               Objects.toString(rol, NULL_VALUE);
    }

    /**
     * Generates a cache key for active terms
     */
    public String generateActiveTermsKey() {
        return "active_terms";
    }

    /**
     * Generates a cache key for terms by type
     */
    public String generateTermsByTypeKey(String type) {
        return "terms_type" + SEPARATOR + Objects.toString(type, NULL_VALUE);
    }

    /**
     * Generates a cache key for term by ID
     */
    public String generateTermByIdKey(Long termId) {
        return "term" + SEPARATOR + Objects.toString(termId, NULL_VALUE);
    }

    /**
     * Generates a cache key for term acceptance verification
     */
    public String generateTermAcceptanceKey(Long userId, Long termId) {
        return "acceptance" + SEPARATOR + 
               Objects.toString(userId, NULL_VALUE) + SEPARATOR +
               Objects.toString(termId, NULL_VALUE);
    }

    /**
     * Generates a cache key for all terms acceptance by email
     */
    public String generateAllTermsAcceptanceKey(String email) {
        return "all_accepted" + SEPARATOR + Objects.toString(email, NULL_VALUE);
    }

    /**
     * Generates a generic cache key from multiple parameters
     */
    public String generateKey(String prefix, Object... params) {
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("Cache key prefix cannot be null or empty");
        }

        if (params == null || params.length == 0) {
            return prefix;
        }

        String paramString = List.of(params)
                .stream()
                .map(param -> Objects.toString(param, NULL_VALUE))
                .collect(Collectors.joining(SEPARATOR));

        return prefix + SEPARATOR + paramString;
    }

    /**
     * Sanitizes cache key to ensure it's valid
     */
    public String sanitizeKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Cache key cannot be null or empty");
        }
        
        // Remove any potentially problematic characters
        return key.replaceAll("[\\s\\n\\r\\t]", "_")
                 .replaceAll("[^a-zA-Z0-9_:.-]", "");
    }
}
