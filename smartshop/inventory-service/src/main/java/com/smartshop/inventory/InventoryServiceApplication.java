package com.smartshop.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

/**
 * InventoryServiceApplication - Stock Management Service
 *
 * <h2>Purpose</h2>
 * Manages product inventory levels. Processes stock reservations from order-service
 * via Kafka. Uses Redis for caching frequently-read inventory data and
 * optimistic locking (@Version) to prevent race conditions in concurrent reservations.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@EnableCaching: Activates Spring's caching abstraction. Without this,
 *       @Cacheable, @CacheEvict, and @CachePut annotations are silently ignored.</li>
 *   <li>Race Conditions in Stock: Without locking, two concurrent orders for the
 *       same product might both read "5 in stock", both reserve 3 units, and
 *       result in -1 actual stock. Optimistic locking with @Version prevents this.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableDiscoveryClient
// @EnableCaching activates Spring's cache abstraction.
// Combined with RedisConfig (which configures a RedisCacheManager), all
// @Cacheable and @CacheEvict annotations will use Redis as the cache store.
@EnableCaching
@EnableRetry
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
