package com.smartshop.order.repository;

import com.smartshop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * OrderRepository - Persistence gateway for Order aggregates.
 *
 * <h2>Purpose</h2>
 * Order-service needs durable state before publishing events so checkout progress can be recovered. JpaRepository
 * supplies CRUD methods and derived queries with minimal boilerplate.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Aggregate persistence: Saving Order cascades its OrderItem children.</li>
 *   <li>User history: findByUserId supports order-history screens.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * OrderServiceImpl creates orders and SagaOrchestrator updates their statuses through this repository.
 *
 * @see Order
 * @author SmartShop Team
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {
    /**
     * Finds all orders placed by a user.
     *
     * @param userId buyer id
     * @return orders for that user, possibly empty
     */
    List<Order> findByUserId(UUID userId);
}
