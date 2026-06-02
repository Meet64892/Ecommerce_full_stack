package com.smartshop.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * InventoryServiceApplication - Starts stock management and reservation APIs.
 *
 * <h2>Purpose</h2>
 * Inventory-service owns stock counts and protects them from race conditions during checkout. It combines PostgreSQL
 * for durable inventory state with Redis caching for fast reads of frequently requested stock data.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-aside: The service reads from cache first and loads from the database on misses.</li>
 *   <li>Optimistic locking: @Version detects concurrent writes across multiple service instances.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order-service publishes order.created events, this service reserves stock, and it replies with inventory.checked.
 *
 * @see com.smartshop.inventory.service.InventoryService
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableCaching
public class InventoryServiceApplication {
    /**
     * Starts the inventory service.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
