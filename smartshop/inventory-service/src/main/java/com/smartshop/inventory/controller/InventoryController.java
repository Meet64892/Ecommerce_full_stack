package com.smartshop.inventory.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InventoryController - Stock query and manual reservation endpoints.
 *
 * <h2>Purpose</h2>
 * Lets clients read current stock (cache-backed) and manually reserve units
 * (the same path the saga uses internally).
 *
 * <h2>How it fits in the system</h2>
 * Reached via the gateway at {@code /api/inventory/**}.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock levels and reservations")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Reads current stock for a product (served from Redis when cached).
     *
     * @param productId the product id
     * @return 200 OK with the stock view
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get stock for a product")
    public ResponseEntity<ApiResponse<InventoryDto>> getStock(
            @Parameter(description = "Product id") @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getStock(productId)));
    }

    /**
     * Reserves units of a product.
     *
     * @param productId the product id
     * @param request   validated reservation quantity
     * @return 200 OK when reserved; 409 Conflict when stock is insufficient
     */
    @PutMapping("/{productId}/reserve")
    @Operation(summary = "Reserve stock", description = "Decrements available stock if enough is in stock.")
    public ResponseEntity<ApiResponse<Boolean>> reserve(
            @Parameter(description = "Product id") @PathVariable Long productId,
            @Valid @RequestBody ReserveStockRequest request) {
        boolean reserved = inventoryService.reserve(productId, request.quantity());
        if (reserved) {
            return ResponseEntity.ok(ApiResponse.of(true, "Stock reserved"));
        }
        // 409 Conflict communicates "the request is valid but conflicts with the
        // current state" (not enough stock) better than a generic 400.
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.of(false, "Insufficient stock"));
    }
}
