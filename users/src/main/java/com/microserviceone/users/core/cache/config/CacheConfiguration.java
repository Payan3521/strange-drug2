package com.microserviceone.users.core.cache.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.microserviceone.users.core.logging.LoggingService;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableCaching
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.cache")
@Data
public class CacheConfiguration {

    private final LoggingService loggingService;
    
    // Cache configuration properties
    private Duration defaultExpiration = Duration.ofMinutes(30);
    private Duration userExpiration = Duration.ofMinutes(15);
    private Duration termsExpiration = Duration.ofHours(2);
    private Duration searchExpiration = Duration.ofMinutes(5);
    private long maximumSize = 1000;
    private boolean recordStats = true;

    // Cache names constants
    public static final String USER_BY_ID_CACHE = "userById";
    public static final String USER_SEARCH_CACHE = "userSearch";
    public static final String TERMS_ACTIVE_CACHE = "termsActive";
    public static final String TERMS_BY_TYPE_CACHE = "termsByType";
    public static final String TERMS_BY_ID_CACHE = "termsById";
    public static final String TERM_ACCEPTANCE_CACHE = "termAcceptance";
    public static final String EMAIL_VERIFIED_CACHE = "emailVerified";
    public static final String USER_EXISTS_BY_EMAIL_CACHE = "userExistsByEmail";

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure individual caches
        cacheManager.registerCustomCache(USER_BY_ID_CACHE, buildUserCache());
        cacheManager.registerCustomCache(USER_SEARCH_CACHE, buildSearchCache());
        cacheManager.registerCustomCache(TERMS_ACTIVE_CACHE, buildTermsCache());
        cacheManager.registerCustomCache(TERMS_BY_TYPE_CACHE, buildTermsCache());
        cacheManager.registerCustomCache(TERMS_BY_ID_CACHE, buildTermsCache());
        cacheManager.registerCustomCache(TERM_ACCEPTANCE_CACHE, buildDefaultCache());
        cacheManager.registerCustomCache(EMAIL_VERIFIED_CACHE, buildDefaultCache());
        cacheManager.registerCustomCache(USER_EXISTS_BY_EMAIL_CACHE, buildUserCache());
        
        cacheManager.setAllowNullValues(false);
        
        loggingService.logInfo("CacheConfiguration: Cache manager configurado con {} caches", 8);
        
        return cacheManager;
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildUserCache() {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(userExpiration.toMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener(createRemovalListener("UserCache"))
                .build();
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildSearchCache() {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize / 2) // Smaller for search results
                .expireAfterWrite(searchExpiration.toMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener(createRemovalListener("SearchCache"))
                .build();
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildTermsCache() {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(termsExpiration.toMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener(createRemovalListener("TermsCache"))
                .build();
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> buildDefaultCache() {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(defaultExpiration.toMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener(createRemovalListener("DefaultCache"))
                .build();
    }

    private RemovalListener<Object, Object> createRemovalListener(String cacheName) {
        return (key, value, cause) -> {
            if (loggingService != null) {
                loggingService.logDebug("CacheConfiguration: Elemento removido del cache {} - Key: {}, Causa: {}", 
                    cacheName, key, cause);
            }
        };
    }
}
