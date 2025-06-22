package com.microserviceone.users.core.rateLimiting.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.config.RateLimitProperties;
import com.microserviceone.users.core.rateLimiting.model.BlockInfo;
import lombok.RequiredArgsConstructor;

/**
 * Servicio para manejar bloqueos de usuarios por endpoint
 */
@Service
@RequiredArgsConstructor
public class BlockService {
    
    private final LoggingService loggingService;
    private final RateLimitProperties rateLimitProperties;
    
    // Cache para almacenar información de bloqueos
    private final ConcurrentHashMap<String, BlockInfo> blockedUsers = new ConcurrentHashMap<>();
    
    /**
     * Verifica si un usuario está actualmente bloqueado para un endpoint específico
     */
    public boolean isUserBlocked(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        BlockInfo blockInfo = blockedUsers.get(blockKey);
        
        if (blockInfo == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if (blockInfo.getBlockedUntil().isAfter(now)) {
            loggingService.logDebug("Usuario bloqueado - Nivel: " + blockInfo.getBlockLevel() + 
                ", Bloqueado hasta: " + blockInfo.getBlockedUntil());
            return true;
        }
        
        // El bloqueo ha expirado, pero mantenemos el nivel para el siguiente bloqueo
        loggingService.logDebug("Bloqueo expirado para usuario: " + userIdentifier + 
            " en endpoint: " + endpoint + " - Nivel anterior: " + blockInfo.getBlockLevel());
        return false;
    }
    
    /**
     * Bloquea un usuario solo para un endpoint específico
     */
    public void blockUser(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        BlockInfo currentBlock = blockedUsers.get(blockKey);
        
        // Incrementar nivel de bloqueo o empezar en 1 si no hay bloqueo previo
        int blockLevel = (currentBlock != null) ? currentBlock.getBlockLevel() + 1 : 1;
        
        // Obtener duración del bloqueo según el nivel
        int blockMinutes = rateLimitProperties.getBlockDuration(blockLevel);
        
        LocalDateTime blockedUntil = LocalDateTime.now().plus(blockMinutes, ChronoUnit.MINUTES);
        
        BlockInfo newBlock = new BlockInfo(blockLevel, blockedUntil);
        blockedUsers.put(blockKey, newBlock);
        
        loggingService.logWarning("Usuario bloqueado para endpoint " + endpoint + ": " + userIdentifier + 
            " - Nivel: " + blockLevel + " - Duración: " + blockMinutes + " minutos" +
            " - Bloqueado hasta: " + blockedUntil);
    }
    
    /**
     * Obtiene información del bloqueo actual de un usuario
     */
    public BlockInfo getBlockInfo(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        return blockedUsers.get(blockKey);
    }
    
    /**
     * Genera una clave única para el bloqueo
     */
    private String generateBlockKey(String userIdentifier, String endpoint) {
        return userIdentifier + ":block:" + endpoint;
    }
} 