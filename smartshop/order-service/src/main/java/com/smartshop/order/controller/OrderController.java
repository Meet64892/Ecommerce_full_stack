package com.smartshop.order.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * OrderController - REST API for checkout and order history.
 *
 * <h2>Purpose</h2>
 * The controller exposes synchronous HTTP endpoints while the actual fulfillment workflow continues asynchronously
 * through Kafka. This keeps client interaction simple without hiding the Saga status.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>202-style thinking: A created order may still be pending while downstream steps complete.</li>
 *   <li>OpenAPI: Operation annotations document the checkout contract.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes `/orders/**` here and OrderService creates/read order aggregates.
 *
 * @see OrderService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Checkout and order history")
public class OrderController {
    private final OrderService orderService;

    /**
     * Creates the controller with constructor injection.
     *
     * @param orderService order application service
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order and starts the Saga.
     *
     * @param request checkout command
     * @return HTTP 201 with pending order data
     */
    @Operation(summary = "Create order", description = "Persists a pending order and publishes order.created.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> create(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderService.createOrder(request), "Order created"));
    }

    /**
     * Loads one order by id.
     *
     * @param id order id
     * @return order DTO
     */
    @Operation(summary = "Get order", description = "Returns an order and its current Saga status.")
    @GetMapping("/{id}")
    public ApiResponse<OrderDto> get(@Parameter(description = "Order id") @PathVariable UUID id) {
        return ApiResponse.success(orderService.getOrder(id), "Order loaded");
    }

    /**
     * Lists orders for a user.
     *
     * @param userId buyer id
     * @return order history list
     */
    @Operation(summary = "Orders for user", description = "Returns all orders placed by a user.")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderDto>> byUser(@PathVariable UUID userId) {
        return ApiResponse.success(orderService.ordersForUser(userId), "Orders loaded");
    }
}
