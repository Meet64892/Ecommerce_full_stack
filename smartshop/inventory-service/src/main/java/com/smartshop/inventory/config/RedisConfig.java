package com.smartshop.inventory.config;

import com.smartshop.inventory.entity.Inventory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * RedisConfig - Configures the Redis cache manager and a typed RedisTemplate.
 *
 * <h2>Purpose</h2>
 * Sets up Redis as the backing store for the Spring Cache abstraction (so
 * {@code @Cacheable} on inventory reads hits Redis) and provides a RedisTemplate
 * for any direct access.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Why Redis as a cache</b>: it is an in-memory key-value store, so reads
 *       are sub-millisecond — far faster than hitting PostgreSQL for hot data.</li>
 *   <li><b>TTL (time-to-live)</b>: entries expire automatically after a set
 *       duration, bounding staleness and freeing memory. Combined with Redis
 *       eviction policies (e.g. allkeys-lru) when memory is full.</li>
 *   <li><b>Cache-aside pattern</b>: the app checks the cache first; on a miss it
 *       loads from the DB and populates the cache. (Alternatives: read-through /
 *       write-through where the cache layer itself talks to the DB.)</li>
 *   <li><b>Serialization</b>: values are stored as JSON so cached objects are
 *       human-readable and language-neutral in Redis.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The cache manager backs {@code @Cacheable("inventory")} in the service; the
 * {@code RedisHealthIndicator} verifies connectivity for readiness.
 *
 * @author SmartShop Team
 */
@Configuration
public class RedisConfig {

    /** Cache TTL (seconds) bound from config; bounds how stale a read can be. */
    @Value("${smartshop.cache.inventory-ttl-seconds:60}")
    private long ttlSeconds;

    /**
     * RedisTemplate for direct String-keyed, JSON-valued access if needed.
     *
     * @param connectionFactory the auto-configured Redis connection factory
     * @return a configured {@link RedisTemplate}
     */
    @Bean
    public RedisTemplate<String, Inventory> inventoryRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Inventory> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Keys as plain strings; values as JSON for readability/interoperability.
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Cache manager that applies our TTL + JSON serialization to all caches.
     *
     * @param connectionFactory the auto-configured Redis connection factory
     * @return a {@link RedisCacheManager}
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // Expire entries after the configured TTL.
                .entryTtl(Duration.ofSeconds(ttlSeconds))
                // Don't cache nulls (avoids caching "not found" as a hit).
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
