package com.smartshop.order.service;

import com.smartshop.order.dto.CreateOrderRequest;
import com.smartshop.order.dto.OrderDto;
import java.util.List;

/**
 * OrderService - Order use-case contract.
 *
 * <h2>Purpose</h2>
 * Defines order API operations independent of transport and persistence details.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Use-case boundary: controllers delegate business logic to this interface.</li>
 *   <li>Status-driven workflows: asynchronous saga updates order states over time.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Implemented by OrderServiceImpl and called by OrderController.
 *
 * @see OrderServiceImpl
 * @author SmartShop Team
 */
public interface OrderService {

    /**
     * Creates an order and starts saga workflow.
     *
     * @param request create payload
     * @return created order dto
     */
    OrderDto createOrder(CreateOrderRequest request);

    /**
     * Gets order by id.
     *
     * @param id order id
     * @return order dto
     */
    OrderDto getById(Long id);

    /**
     * Lists orders by user id.
     *
     * @param userId user id
     * @return order list
     */
    List<OrderDto> getByUserId(Long userId);
}
