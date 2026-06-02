package com.smartshop.inventory.service;

import com.smartshop.inventory.dto.InventoryDto;

import java.util.UUID;

/**
 * InventoryService - Application contract for stock reads and reservations.
 *
 * <h2>Purpose</h2>
 * The service boundary hides caching, transactions, and concurrency control from controllers and Kafka consumers.
 * This keeps every caller using the same stock reservation rules.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-aside: Reads can be cached while writes evict stale entries.</li>
 *   <li>Transaction boundary: Reservation is one database unit of work.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryController and OrderEventConsumer both call this interface.
 *
 * @see InventoryServiceImpl
 * @author SmartShop Team
 */
public interface InventoryService {
    /**
     * Reads inventory by product id.
     *
     * @param productId product id
     * @return inventory DTO
     */
    InventoryDto getInventory(UUID productId);

    /**
     * Reserves stock for a product.
     *
     * @param productId product id
     * @param quantity quantity to reserve
     * @return updated inventory DTO
     */
    InventoryDto reserve(UUID productId, int quantity);
}
