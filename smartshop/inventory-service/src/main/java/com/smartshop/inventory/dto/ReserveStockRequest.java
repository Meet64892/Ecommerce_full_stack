package com.smartshop.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * ReserveStockRequest - Payload for reserve stock operation.
 *
 * <h2>Purpose</h2>
 * Defines quantity and order context required to reserve stock safely.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Bean Validation: prevents invalid quantities early.</li>
 *   <li>Order linkage: enables auditing and idempotency strategies.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by REST endpoint and Kafka event flow.
 *
 * @see com.smartshop.inventory.controller.InventoryController
 * @author SmartShop Team
 */
public record ReserveStockRequest(
        @NotNull Long orderId,
        @NotNull @Min(1) Integer quantity
) {
}
