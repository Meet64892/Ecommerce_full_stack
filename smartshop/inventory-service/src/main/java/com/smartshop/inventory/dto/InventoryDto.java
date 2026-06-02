package com.smartshop.inventory.dto;

/**
 * InventoryDto - Outbound stock view for a product.
 *
 * <h2>Purpose</h2>
 * Exposes only the fields clients care about (product + available quantity),
 * hiding the internal version column.
 *
 * @param productId         the product
 * @param availableQuantity units currently available
 * @author SmartShop Team
 */
public record InventoryDto(
        Long productId,
        int availableQuantity
) {
}
