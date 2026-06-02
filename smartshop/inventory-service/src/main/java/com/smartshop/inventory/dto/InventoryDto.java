package com.smartshop.inventory.dto;

/**
 * InventoryDto - Safe Public Representation of Inventory Data
 *
 * @author SmartShop Team
 */
public record InventoryDto(
    Long id,
    Long productId,
    String sku,
    int quantity,
    int reservedQuantity,
    int availableQuantity,
    int lowStockThreshold,
    boolean lowStock
) {}
