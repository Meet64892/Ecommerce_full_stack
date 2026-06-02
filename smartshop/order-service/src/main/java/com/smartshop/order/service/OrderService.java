package com.smartshop.order.service;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;

import java.util.List;
import java.util.UUID;

/**
 * OrderService - Application service contract for checkout and history.
 *
 * <h2>Purpose</h2>
 * The interface defines order use cases and keeps controllers independent from persistence and Kafka details.
 * Transactional behavior belongs in the implementation because it coordinates database writes and Saga starts.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Command handling: createOrder turns user intent into a durable pending order.</li>
 *   <li>Query handling: read methods return DTOs without mutating state.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderController calls this interface and OrderServiceImpl handles mapping, persistence, and orchestration.
 *
 * @see OrderServiceImpl
 * @author SmartShop Team
 */
public interface OrderService {
    /**
     * Creates an order and starts the Saga.
     *
     * @param request validated checkout command
     * @return created pending order
     */
    OrderDto createOrder(CreateOrderRequest request);

    /**
     * Loads one order.
     *
     * @param id order id
     * @return order DTO
     */
    OrderDto getOrder(UUID id);

    /**
     * Lists orders for a user.
     *
     * @param userId buyer id
     * @return order DTOs
     */
    List<OrderDto> ordersForUser(UUID userId);
}
