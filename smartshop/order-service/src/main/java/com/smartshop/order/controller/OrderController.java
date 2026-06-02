package com.smartshop.order.controller;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * OrderController - Order API endpoints.
 *
 * <h2>Purpose</h2>
 * Exposes order creation and query endpoints for clients and internal tools.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Async flow: create endpoint returns PENDING while saga completes later.</li>
 *   <li>Explicit query endpoints: supports polling for eventual consistency updates.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * API gateway forwards /orders traffic to this controller.
 *
 * @see com.smartshop.order.service.OrderService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order.
     *
     * @param request create request
     * @return created order dto
     */
    @Operation(summary = "Create order")
    @ApiResponse(responseCode = "200", description = "Order accepted")
    @PostMapping
    public OrderDto create(@Valid @RequestBody final CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    /**
     * Gets order by id.
     *
     * @param id order id
     * @return order dto
     */
    @Operation(summary = "Get order by id")
    @ApiResponse(responseCode = "200", description = "Order returned")
    @GetMapping("/{id}")
    public OrderDto getById(@Parameter(description = "Order ID") @PathVariable final Long id) {
        return orderService.getById(id);
    }

    /**
     * Gets all orders for one user.
     *
     * @param userId user id
     * @return order list
     */
    @Operation(summary = "Get orders by user id")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    @GetMapping("/user/{userId}")
    public List<OrderDto> getByUserId(@PathVariable final Long userId) {
        return orderService.getByUserId(userId);
    }
}
