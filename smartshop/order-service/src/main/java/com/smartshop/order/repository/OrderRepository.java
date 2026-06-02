package com.smartshop.order.repository;

import com.smartshop.order.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrderRepository - JPA repository for order aggregates.
 *
 * <h2>Purpose</h2>
 * Encapsulates persistence operations for orders and common lookup queries by user.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Repository abstraction: no SQL leakage into service layer.</li>
 *   <li>Aggregate retrieval: returns full order graph with items as needed.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by OrderService and saga handlers to read/write order state.
 *
 * @see com.smartshop.order.service.OrderServiceImpl
 * @author SmartShop Team
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves all orders for one user.
     *
     * @param userId user id
     * @return user order list
     */
    List<Order> findByUserId(Long userId);
}
