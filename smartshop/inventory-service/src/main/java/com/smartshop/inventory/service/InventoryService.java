package com.smartshop.inventory.service;

import com.smartshop.inventory.dto.InventoryDto;

/**
 * InventoryService - Inventory use-case contract.
 *
 * <h2>Purpose</h2>
 * Defines inventory read and reservation operations.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Cache-aware reads: reduce latency and database load.</li>
 *   <li>Transactional reservations: update stock atomically.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by controller and Kafka consumer for stock operations.
 *
 * @see InventoryServiceImpl
 * @author SmartShop Team
 */
public interface InventoryService {

    /**
     * Returns inventory for product.
     *
     * @param productId product id
     * @return inventory dto
     */
    InventoryDto getInventory(Long productId);

    /**
     * Reserves stock units for an order.
     *
     * @param productId product id
     * @param quantity units to reserve
     * @return updated inventory dto
     */
    InventoryDto reserveStock(Long productId, Integer quantity);
}
