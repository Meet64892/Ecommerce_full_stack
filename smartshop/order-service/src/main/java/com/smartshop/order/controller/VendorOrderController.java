package com.smartshop.order.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.common.exception.ValidationException;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.UpdateOrderStatusRequest;
import com.smartshop.order.security.MarketplaceContext;
import com.smartshop.order.security.MarketplacePrincipal;
import com.smartshop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/vendor/orders")
@Tag(name = "Vendor Orders", description = "Brand-scoped order fulfillment")
public class VendorOrderController {
    private final OrderService orderService;

    public VendorOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "List orders containing my brand products")
    @GetMapping
    public ApiResponse<Page<OrderDto>> list(Pageable pageable) {
        UUID brandId = requireVendorBrandId();
        return ApiResponse.success(orderService.listOrdersForBrand(brandId, pageable), "Vendor orders loaded");
    }

    @Operation(summary = "Get order details for my brand lines")
    @GetMapping("/{id}")
    public ApiResponse<OrderDto> get(@PathVariable UUID id) {
        UUID brandId = requireVendorBrandId();
        return ApiResponse.success(orderService.getOrderForBrand(id, brandId), "Order loaded");
    }

    @Operation(summary = "Update fulfillment status for an order with my products")
    @PatchMapping("/{id}/status")
    public ApiResponse<OrderDto> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        UUID brandId = requireVendorBrandId();
        return ApiResponse.success(
                orderService.updateStatusAsVendor(id, brandId, request.status()),
                "Order status updated");
    }

    private static UUID requireVendorBrandId() {
        MarketplacePrincipal principal = MarketplaceContext.get();
        if (principal == null || !principal.isVendor() || principal.brandId() == null) {
            throw new ValidationException("Approved vendor brand required");
        }
        return principal.brandId();
    }
}
