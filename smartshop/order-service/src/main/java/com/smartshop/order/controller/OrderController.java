package com.smartshop.order.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.service.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OrderController - REST Endpoints for Order Management
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order placement and management")
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /orders for userId={}", userId);
        OrderDto order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order placed successfully. Processing..."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<ApiResponse<Page<OrderDto>>> getUserOrders(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByUserId(userId, pageable)));
    }
}
