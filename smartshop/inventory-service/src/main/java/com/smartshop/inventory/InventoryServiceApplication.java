package com.smartshop.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

/**
 * InventoryServiceApplication - Bootstraps the stock-management service.
 *
 * <h2>Purpose</h2>
 * Tracks per-product stock, reserves it when orders arrive, and caches hot reads
 * in Redis. It is the participant the order saga waits on.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@EnableCaching</b>: activates Spring's cache abstraction so
 *       {@code @Cacheable}/{@code @CacheEvict} annotations are honored (backed by
 *       Redis here).</li>
 *   <li><b>@EnableRetry</b>: activates spring-retry so {@code @Retryable} on the
 *       reservation method transparently retries optimistic-lock conflicts.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes {@code order.created}, replies on {@code inventory.checked}, and
 * serves stock reads via the gateway at {@code /api/inventory/**}.
 *
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableCaching
@EnableRetry
public class InventoryServiceApplication {

    /**
     * Spring Boot entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
