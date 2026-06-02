package com.smartshop.inventory.dto;

import java.util.UUID;

/**
 * InventoryDto - Public inventory snapshot.
 *
 * <h2>Purpose</h2>
 * The DTO exposes current inventory state without giving clients a mutable entity. It includes both total and reserved
 * quantities so operators can understand why salable quantity may be lower than stock on hand.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Snapshot: Represents inventory at the moment of the read.</li>
 *   <li>Record: Immutable Java data carrier for API responses.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryController returns this record after service reads or reservations.
 *
 * @see ReserveStockRequest
 * @author SmartShop Team
 */
public record InventoryDto(UUID productId, int quantityAvailable, int quantityReserved, int salableQuantity, long version) {
}
