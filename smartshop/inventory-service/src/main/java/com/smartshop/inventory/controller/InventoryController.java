package com.smartshop.inventory.controller;

import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * InventoryController - Inventory HTTP endpoints.
 *
 * <h2>Purpose</h2>
 * Exposes inventory read and reserve APIs for synchronous calls and diagnostics.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Thin controller pattern: business logic remains in service layer.</li>
 *   <li>Validation-first endpoint handling.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes /inventory requests here.
 *
 * @see com.smartshop.inventory.service.InventoryService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Returns product inventory.
     *
     * @param productId product id
     * @return inventory dto
     */
    @Operation(summary = "Get inventory by product id")
    @ApiResponse(responseCode = "200", description = "Inventory returned")
    @GetMapping("/{productId}")
    public InventoryDto getInventory(@PathVariable final Long productId) {
        return inventoryService.getInventory(productId);
    }

    /**
     * Reserves stock units for product.
     *
     * @param productId product id
     * @param request reserve payload
     * @return updated inventory
     */
    @Operation(summary = "Reserve stock")
    @ApiResponse(responseCode = "200", description = "Stock reserved")
    @PutMapping("/{productId}/reserve")
    public InventoryDto reserve(@PathVariable final Long productId, @Valid @RequestBody final ReserveStockRequest request) {
        return inventoryService.reserveStock(productId, request.quantity());
    }
}
