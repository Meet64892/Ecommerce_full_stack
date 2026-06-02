package com.smartshop.order.repository;

import com.smartshop.order.entity.Order;
import com.smartshop.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * OrderRepository - Spring Data JPA Repository for Order Persistence
 *
 * @author SmartShop Team
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByCorrelationId(String correlationId);
}
