package com.smartshop.order.service;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;

import java.util.List;

/**
 * OrderService - Business operations for orders.
 *
 * <h2>Purpose</h2>
 * Contract for placing and reading orders. Placement also triggers the saga.
 *
 * <h2>How it fits in the system</h2>
 * Implemented by {@code OrderServiceImpl}; used by {@code OrderController}.
 *
 * @author SmartShop Team
 */
public interface OrderService {

    /**
     * Places a new order (status PENDING) and starts the inventory saga.
     *
     * @param request validated order request
     * @return the created order view (initially PENDING)
     */
    OrderDto placeOrder(CreateOrderRequest request);

    /**
     * @param id order id
     * @return the order view
     * @throws com.smartshop.common.exception.ResourceNotFoundException if absent
     */
    OrderDto getById(Long id);

    /**
     * @param userId customer id
     * @return that customer's orders
     */
    List<OrderDto> getByUser(Long userId);
}
