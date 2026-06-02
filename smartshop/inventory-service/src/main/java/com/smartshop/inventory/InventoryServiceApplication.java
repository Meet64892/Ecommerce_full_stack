package com.smartshop.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * InventoryServiceApplication - Stock management and reservation service bootstrap.
 *
 * <h2>Purpose</h2>
 * Maintains product stock levels and reservation logic for order workflows.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-enabled reads: Redis reduces repetitive database load.</li>
 *   <li>Concurrency control: optimistic locking prevents overselling under race conditions.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Consumes order.created events, reserves stock, emits inventory.checked responses.
 *
 * @see com.smartshop.inventory.service.InventoryServiceImpl
 * @author SmartShop Team
 */
@SpringBootApplication
@EnableCaching
public class InventoryServiceApplication {

    /**
     * Starts inventory service.
     *
     * @param args startup args
     * @return nothing
     */
    public static void main(final String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
