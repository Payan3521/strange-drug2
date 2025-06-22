package com.microserviceone.users.core.cache.service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Generic cache service interface for handling caching operations
 * Provides a clean abstraction over the underlying cache implementation
 */
public interface CacheService {
    
    /**
     * Retrieves a value from cache or computes it if not present
     * 
     * @param cacheName the name of the cache
     * @param key the cache key
     * @param valueSupplier supplier to compute the value if not in cache
     * @param valueType the type of the cached value
     * @return the cached or computed value
     */
    <T> Optional<T> getOrCompute(String cacheName, String key, Supplier<T> valueSupplier, Class<T> valueType);
    
    /**
     * Retrieves a value from cache
     * 
     * @param cacheName the name of the cache
     * @param key the cache key
     * @param valueType the type of the cached value
     * @return the cached value if present
     */
    <T> Optional<T> get(String cacheName, String key, Class<T> valueType);
    
    /**
     * Stores a value in cache
     * 
     * @param cacheName the name of the cache
     * @param key the cache key
     * @param value the value to cache
     */
    void put(String cacheName, String key, Object value);
    
    /**
     * Removes a value from cache
     * 
     * @param cacheName the name of the cache
     * @param key the cache key
     */
    void evict(String cacheName, String key);
    
    /**
     * Clears all entries from a specific cache
     * 
     * @param cacheName the name of the cache to clear
     */
    void clear(String cacheName);
    
    /**
     * Clears all caches
     */
    void clearAll();
    
    /**
     * Gets cache statistics for monitoring
     * 
     * @param cacheName the name of the cache
     * @return cache statistics as a formatted string
     */
    String getCacheStats(String cacheName);
}
