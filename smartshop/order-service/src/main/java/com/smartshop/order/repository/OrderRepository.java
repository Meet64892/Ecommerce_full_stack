package com.smartshop.order.repository;

import com.smartshop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderRepository - Spring Data JPA repository for {@link Order}.
 *
 * <h2>Purpose</h2>
 * Persists orders and supports fetching a user's order history.
 *
 * <h2>How it fits in the system</h2>
 * Used by {@code OrderServiceImpl} and the saga to load/save order state.
 *
 * @author SmartShop Team
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * @param userId the customer id
     * @return that customer's orders
     */
    List<Order> findByUserId(Long userId);
}
