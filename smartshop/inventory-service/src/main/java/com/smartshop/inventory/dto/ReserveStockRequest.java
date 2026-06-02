package com.smartshop.inventory.dto;

import jakarta.validation.constraints.Min;

/**
 * ReserveStockRequest - Command payload for reserving stock.
 *
 * <h2>Purpose</h2>
 * Reservations must specify a positive quantity. Keeping the request as a DTO makes it impossible for clients to set
 * internal fields such as version or quantityReserved directly.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation: @Min rejects zero and negative reservations.</li>
 *   <li>Command DTO: Represents the requested action rather than database state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryController and Kafka consumers call InventoryService with this quantity.
 *
 * @see InventoryDto
 * @author SmartShop Team
 */
public record ReserveStockRequest(@Min(1) int quantity) {
}
