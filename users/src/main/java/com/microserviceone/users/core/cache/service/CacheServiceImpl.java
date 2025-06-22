package com.microserviceone.users.core.cache.service;

import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;
    private final LoggingService loggingService;

    @Override
    public <T> Optional<T> getOrCompute(String cacheName, String key, Supplier<T> valueSupplier, Class<T> valueType) {
        try {
            loggingService.logDebug("CacheService: getOrCompute - Cache: {}, Key: {}", cacheName, key);
            
            Cache cache = getCache(cacheName);
            if (cache == null) {
                loggingService.logWarning("CacheService: Cache no encontrado: {}, ejecutando directamente", cacheName);
                return Optional.ofNullable(valueSupplier.get());
            }

            // Try to get from cache first
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null && wrapper.get() != null) {
                loggingService.logDebug("CacheService: Cache HIT - Cache: {}, Key: {}", cacheName, key);
                return Optional.of(valueType.cast(wrapper.get()));
            }

            // Not in cache, compute value
            loggingService.logDebug("CacheService: Cache MISS - Cache: {}, Key: {}", cacheName, key);
            T value = valueSupplier.get();
            
            if (value != null) {
                cache.put(key, value);
                loggingService.logDebug("CacheService: Valor almacenado en cache - Cache: {}, Key: {}", cacheName, key);
            }
            
            return Optional.ofNullable(value);
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error en getOrCompute - Cache: {}, Key: {}", cacheName, key, e);
            // Fallback to direct computation
            return Optional.ofNullable(valueSupplier.get());
        }
    }

    @Override
    public <T> Optional<T> get(String cacheName, String key, Class<T> valueType) {
        try {
            loggingService.logDebug("CacheService: get - Cache: {}, Key: {}", cacheName, key);
            
            Cache cache = getCache(cacheName);
            if (cache == null) {
                return Optional.empty();
            }

            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null && wrapper.get() != null) {
                loggingService.logDebug("CacheService: Valor encontrado en cache - Cache: {}, Key: {}", cacheName, key);
                return Optional.of(valueType.cast(wrapper.get()));
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error en get - Cache: {}, Key: {}", cacheName, key, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String cacheName, String key, Object value) {
        try {
            loggingService.logDebug("CacheService: put - Cache: {}, Key: {}", cacheName, key);
            
            Cache cache = getCache(cacheName);
            if (cache != null && value != null) {
                cache.put(key, value);
                loggingService.logDebug("CacheService: Valor almacenado - Cache: {}, Key: {}", cacheName, key);
            }
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error en put - Cache: {}, Key: {}", cacheName, key, e);
        }
    }

    @Override
    public void evict(String cacheName, String key) {
        try {
            loggingService.logDebug("CacheService: evict - Cache: {}, Key: {}", cacheName, key);
            
            Cache cache = getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                loggingService.logDebug("CacheService: Entrada removida del cache - Cache: {}, Key: {}", cacheName, key);
            }
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error en evict - Cache: {}, Key: {}", cacheName, key, e);
        }
    }

    @Override
    public void clear(String cacheName) {
        try {
            loggingService.logInfo("CacheService: clear - Cache: {}", cacheName);
            
            Cache cache = getCache(cacheName);
            if (cache != null) {
                cache.clear();
                loggingService.logInfo("CacheService: Cache limpiado - Cache: {}", cacheName);
            }
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error en clear - Cache: {}", cacheName, e);
        }
    }

    @Override
    public void clearAll() {
        try {
            loggingService.logInfo("CacheService: Limpiando todos los caches");
            
            cacheManager.getCacheNames().forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                }
            });
            
            loggingService.logInfo("CacheService: Todos los caches limpiados");
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error al limpiar todos los caches", e);
        }
    }

    @Override
    public String getCacheStats(String cacheName) {
        try {
            Cache cache = getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();
                
                return String.format(
                    "Cache: %s - Hits: %d, Misses: %d, Hit Rate: %.2f%%, Evictions: %d, Size: %d",
                    cacheName,
                    stats.hitCount(),
                    stats.missCount(),
                    stats.hitRate() * 100,
                    stats.evictionCount(),
                    nativeCache.estimatedSize()
                );
            }
            return "Cache statistics not available for: " + cacheName;
            
        } catch (Exception e) {
            loggingService.logError("CacheService: Error obteniendo estadísticas - Cache: {}", cacheName, e);
            return "Error retrieving stats for: " + cacheName;
        }
    }

    private Cache getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }
}
