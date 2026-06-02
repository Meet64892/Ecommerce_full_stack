package com.smartshop.inventory.config;

import org.springframework.cache.CacheManager;
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
 * RedisConfig - Configures Redis templates and cache TTLs.
 *
 * <h2>Purpose</h2>
 * Redis is an in-memory data store used as a cache because it provides very low-latency reads, TTL expiration, and
 * eviction policies. Inventory uses cache-aside: read cache, load database on miss, then cache the result.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>TTL: Time-to-live automatically expires stale entries.</li>
 *   <li>Invalidation: Writes evict cached inventory so future reads see fresh database state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * @Cacheable and @CacheEvict annotations in InventoryServiceImpl use the CacheManager defined here.
 *
 * @see com.smartshop.inventory.service.InventoryServiceImpl
 * @author SmartShop Team
 */
@Configuration
public class RedisConfig {
    /**
     * Creates a RedisTemplate for explicit Redis operations.
     *
     * @param connectionFactory Redis connection factory created by Spring Boot
     * @return template with String keys and JSON values
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Creates a cache manager with a short TTL for inventory snapshots.
     *
     * @param connectionFactory Redis connection factory
     * @return cache manager used by Spring Cache
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(configuration).build();
    }
}
