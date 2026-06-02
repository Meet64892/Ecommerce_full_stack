package com.smartshop.inventory.dto;

import jakarta.validation.constraints.Min;

/**
 * ReserveStockRequest - Payload to reserve units of a product.
 *
 * <h2>Purpose</h2>
 * Carries how many units to reserve in a manual reservation call.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Min(1)} rejects non-positive reservations.</li>
 * </ul>
 *
 * @param quantity number of units to reserve (>= 1)
 * @author SmartShop Team
 */
public record ReserveStockRequest(

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity
) {
}
