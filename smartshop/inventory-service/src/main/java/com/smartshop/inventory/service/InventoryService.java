package com.smartshop.inventory.service;

import com.smartshop.inventory.dto.InventoryDto;

/**
 * InventoryService - Stock read/reserve operations.
 *
 * <h2>Purpose</h2>
 * Contract for querying stock and attempting reservations (the saga step).
 *
 * <h2>How it fits in the system</h2>
 * Implemented by {@code InventoryServiceImpl}; used by the controller and the
 * Kafka consumer.
 *
 * @author SmartShop Team
 */
public interface InventoryService {

    /**
     * Reads current stock for a product (cached).
     *
     * @param productId the product
     * @return the stock view
     * @throws com.smartshop.common.exception.ResourceNotFoundException if absent
     */
    InventoryDto getStock(Long productId);

    /**
     * Attempts to reserve units, decrementing available stock.
     *
     * @param productId the product
     * @param quantity  units to reserve
     * @return true if reserved; false if insufficient stock
     */
    boolean reserve(Long productId, int quantity);
}
