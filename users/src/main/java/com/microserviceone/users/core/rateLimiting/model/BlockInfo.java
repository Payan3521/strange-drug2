package com.microserviceone.users.core.rateLimiting.model;

import java.time.LocalDateTime;

/**
 * Clase para manejar información de bloqueos de usuarios
 */
public class BlockInfo {
    private final int blockLevel;
    private final LocalDateTime blockedUntil;
    
    public BlockInfo(int blockLevel, LocalDateTime blockedUntil) {
        this.blockLevel = blockLevel;
        this.blockedUntil = blockedUntil;
    }
    
    public int getBlockLevel() {
        return blockLevel;
    }
    
    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }
} 