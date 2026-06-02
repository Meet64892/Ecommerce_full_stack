package com.smartshop.inventory.config;

import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisConfig - Redis template and cache manager setup.
 *
 * <h2>Purpose</h2>
 * Redis is an in-memory datastore ideal for low-latency caching. TTL-based eviction prevents stale
 * cache entries from persisting indefinitely.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-aside pattern: app fetches DB on cache miss then populates cache.</li>
 *   <li>TTL invalidation: time-based eviction keeps data reasonably fresh.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Backing cache for inventory reads and gateway rate-limiter keys.
 *
 * @see com.smartshop.inventory.service.InventoryServiceImpl
 * @author SmartShop Team
 */
@Configuration
public class RedisConfig {

    /**
     * Redis template for object caching operations.
     *
     * @param connectionFactory redis connection factory
     * @return configured redis template
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Cache manager with TTL configuration.
     *
     * @param connectionFactory redis connection factory
     * @return cache manager
     */
    @Bean
    public CacheManager cacheManager(final RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }
}
