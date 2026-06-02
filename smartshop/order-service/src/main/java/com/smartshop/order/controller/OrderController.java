package com.smartshop.order.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import com.smartshop.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OrderController - Endpoints for placing and reading orders.
 *
 * <h2>Purpose</h2>
 * Accepts new orders (kicking off the saga) and exposes order/history reads.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>A successful POST returns 201 with the order in PENDING — confirmation
 *       happens asynchronously via the saga, illustrating eventual consistency.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reached via the gateway at {@code /api/orders/**} (behind a circuit breaker).
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and lifecycle")
public class OrderController {

    private final OrderService orderService;

    /**
     * Places an order.
     *
     * @param request validated order request
     * @return 201 Created with the PENDING order
     */
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Creates a PENDING order and starts the inventory reservation saga.")
    public ResponseEntity<ApiResponse<OrderDto>> place(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto created = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(created, "Order placed; awaiting confirmation"));
    }

    /**
     * @param id order id
     * @return 200 OK with the order
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an order by id")
    public ResponseEntity<ApiResponse<OrderDto>> getById(
            @Parameter(description = "Order id") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getById(id)));
    }

    /**
     * @param userId customer id
     * @return 200 OK with the customer's orders
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "List a user's orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getByUser(
            @Parameter(description = "User id") @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getByUser(userId)));
    }
}
