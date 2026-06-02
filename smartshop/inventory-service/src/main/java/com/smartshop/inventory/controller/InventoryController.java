package com.smartshop.inventory.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * InventoryController - REST API for stock reads and manual reservation.
 *
 * <h2>Purpose</h2>
 * The controller exposes inventory state for product pages and reservation operations for internal workflows. It keeps
 * HTTP mapping separate from cache and locking concerns.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Read endpoint: Uses cache-aside service behavior for fast stock lookup.</li>
 *   <li>Write endpoint: Triggers cache eviction and optimistic locking.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes `/inventory/**` here, while Kafka consumers reuse the same service logic internally.
 *
 * @see InventoryService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Stock lookup and reservation")
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * Creates the controller with constructor injection.
     *
     * @param inventoryService stock application service
     */
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Gets inventory for a product.
     *
     * @param productId product id
     * @return inventory snapshot
     */
    @Operation(summary = "Get inventory", description = "Returns stock levels for a product.")
    @GetMapping("/{productId}")
    public ApiResponse<InventoryDto> get(@Parameter(description = "Product id") @PathVariable UUID productId) {
        return ApiResponse.success(inventoryService.getInventory(productId), "Inventory loaded");
    }

    /**
     * Reserves stock for a product.
     *
     * @param productId product id
     * @param request reservation quantity
     * @return updated inventory snapshot
     */
    @Operation(summary = "Reserve stock", description = "Reserves stock using optimistic locking.")
    @PutMapping("/{productId}/reserve")
    public ApiResponse<InventoryDto> reserve(@PathVariable UUID productId, @Valid @RequestBody ReserveStockRequest request) {
        return ApiResponse.success(inventoryService.reserve(productId, request.quantity()), "Stock reserved");
    }
}
