package com.microserviceone.users.core.rateLimiting.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase para manejar contadores de requests por usuario/endpoint
 */
public class RequestCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    private LocalDateTime lastReset = LocalDateTime.now();
    
    public AtomicInteger getCount() {
        return count;
    }
    
    public LocalDateTime getLastReset() {
        return lastReset;
    }
    
    public int increment() {
        return count.incrementAndGet();
    }
    
    public void reset(LocalDateTime resetTime) {
        count.set(0);
        lastReset = resetTime;
    }
} 