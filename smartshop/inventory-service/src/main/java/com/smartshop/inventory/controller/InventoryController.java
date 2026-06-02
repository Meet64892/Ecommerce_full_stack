package com.smartshop.inventory.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.inventory.dto.InventoryDto;
import com.smartshop.inventory.dto.ReserveStockRequest;
import com.smartshop.inventory.service.InventoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * InventoryController - REST Endpoints for Inventory Management
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Product stock management")
public class InventoryController {

    private final InventoryServiceImpl inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory for a product")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryByProductId(productId)));
    }

    @PutMapping("/{productId}/reserve")
    @Operation(summary = "Reserve stock for an order")
    public ResponseEntity<ApiResponse<InventoryDto>> reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody ReserveStockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.reserveStock(request)));
    }

    @PutMapping("/{productId}/stock")
    @Operation(summary = "Update total stock quantity")
    public ResponseEntity<ApiResponse<InventoryDto>> updateStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.updateStock(productId, quantity)));
    }
}
