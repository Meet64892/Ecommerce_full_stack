package com.smartshop.inventory.dto;

/**
 * InventoryDto - Inventory read projection.
 *
 * <h2>Purpose</h2>
 * Provides safe inventory data to API consumers without exposing mutable entity internals.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>DTO immutability: avoids accidental mutation after service return.</li>
 *   <li>API boundary: isolates persistence model from external contracts.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Returned by inventory endpoints and reservation responses.
 *
 * @see ReserveStockRequest
 * @author SmartShop Team
 */
public record InventoryDto(Long productId, Integer availableQuantity, Integer reservedQuantity) {
}
