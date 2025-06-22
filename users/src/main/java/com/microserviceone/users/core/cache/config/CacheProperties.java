package com.microserviceone.users.core.cache.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * External configuration properties for cache settings
 * Allows runtime configuration through application.yml or environment variables
 */
@Configuration
@ConfigurationProperties(prefix = "app.cache")
@Data
public class CacheProperties {

    /**
     * Global cache settings
     */
    private boolean enabled = true;
    private boolean recordStats = true;
    private long maximumSize = 1000;
    
    /**
     * Default expiration settings
     */
    private Duration defaultExpiration = Duration.ofMinutes(30);
    
    /**
     * User cache specific settings
     */
    private UserCache user = new UserCache();
    
    /**
     * Terms cache specific settings
     */
    private TermsCache terms = new TermsCache();
    
    /**
     * Search cache specific settings
     */
    private SearchCache search = new SearchCache();

    @Data
    public static class UserCache {
        private Duration expiration = Duration.ofMinutes(15);
        private long maximumSize = 1000;
        private boolean enabled = true;
    }

    @Data
    public static class TermsCache {
        private Duration expiration = Duration.ofHours(2);
        private long maximumSize = 500;
        private boolean enabled = true;
    }

    @Data
    public static class SearchCache {
        private Duration expiration = Duration.ofMinutes(5);
        private long maximumSize = 500;
        private boolean enabled = true;
    }
}