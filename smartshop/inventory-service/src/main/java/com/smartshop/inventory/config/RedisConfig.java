package com.smartshop.inventory.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
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
import java.util.HashMap;
import java.util.Map;

/**
 * RedisConfig - Redis Connection and Cache Configuration
 *
 * <h2>Purpose</h2>
 * Configures Redis as a caching layer for inventory data.
 * Without caching, every inventory check would hit PostgreSQL.
 * With Redis, most reads return from memory in ~1ms instead of ~5-10ms from DB.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Redis as a Cache:
 *       Redis is an in-memory data structure store with optional persistence.
 *       Key characteristics:
 *       - Sub-millisecond latency (data is in RAM, not on disk)
 *       - TTL (Time To Live): keys automatically expire after the set duration
 *       - Eviction policies: LRU, LFU, ALLKEYS-LRU (when memory is full)
 *       - Supports String, Hash, List, Set, ZSet data structures</li>
 *   <li>Cache Serialization: Spring's Redis cache stores Java objects as JSON.
 *       GenericJackson2JsonRedisSerializer converts objects to/from JSON.
 *       It embeds the Java class name in the JSON for correct deserialization:
 *       {"@class": "com.smartshop.inventory.dto.InventoryDto", "productId": 1, ...}</li>
 *   <li>TTL Configuration:
 *       Default TTL = 5 minutes. If product stock changes within 5 minutes,
 *       the cache may return stale data until TTL expires or @CacheEvict is called.
 *       We mitigate this with @CacheEvict on every write operation.
 *       inventory cache = 5 min (stock changes occasionally)
 *       Different caches can have different TTLs via per-cache configuration.</li>
 *   <li>RedisTemplate: Low-level Redis operations (get, set, delete, pub/sub).
 *       Spring Cache (@Cacheable) uses CacheManager which internally uses RedisTemplate.
 *       Use RedisTemplate directly for custom operations not covered by annotations.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Configuration
public class RedisConfig {

    /**
     * Configures a RedisTemplate for general-purpose Redis operations.
     * Key serializer: StringRedisSerializer — keys are human-readable strings.
     * Value serializer: JSON — values are serialized as JSON for readability.
     *
     * @param connectionFactory the Redis connection factory (auto-configured by Spring Boot)
     * @return configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Keys: use String serializer (human-readable in Redis CLI)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Values: use JSON serializer (human-readable, supports any object type)
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Configures Spring's CacheManager to use Redis.
     * This is the bean that @Cacheable, @CacheEvict etc. use internally.
     * Per-cache TTL configuration allows fine-tuned expiration policies.
     *
     * @param connectionFactory the Redis connection factory
     * @return configured RedisCacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default configuration for all caches
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))           // Default TTL: 5 minutes
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(createJsonSerializer()))
                // Don't cache null values (prevents null from being stored in Redis)
                // When a product is not found, we don't want to cache "not found" for 5 min
                .disableCachingNullValues();

        // Per-cache TTL overrides
        // inventory cache: 5 minutes (stock changes relatively infrequently)
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("inventory", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Creates a JSON serializer for Redis values that includes type information.
     * Type information is embedded in the JSON as "@class" field, enabling correct
     * deserialization without knowing the target type in advance.
     *
     * @return Jackson-based JSON serializer for Redis
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule for Instant, LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        // Embed the Java class name in the JSON for polymorphic deserialization
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
