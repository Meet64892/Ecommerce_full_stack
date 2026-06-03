package com.smartshop.order.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.dto.UpdateOrderStatusRequest;
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
@RequestMapping("/admin/orders")
@Tag(name = "Admin Orders", description = "Super Admin platform-wide order management")
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "List all platform orders")
    @GetMapping
    public ApiResponse<Page<OrderDto>> list(Pageable pageable) {
        return ApiResponse.success(orderService.listAllOrders(pageable), "Orders loaded");
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{id}")
    public ApiResponse<OrderDto> get(@PathVariable UUID id) {
        return ApiResponse.success(orderService.getOrder(id), "Order loaded");
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/{id}/status")
    public ApiResponse<OrderDto> updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ApiResponse.success(orderService.updateStatusAsAdmin(id, request.status()), "Order status updated");
    }
}
