package com.smartshop.order.repository;

import com.smartshop.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * OrderRepository - Persistence gateway for Order aggregates.
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.brandId = :brandId ORDER BY o.createdAt DESC")
    Page<Order> findDistinctByBrandId(@Param("brandId") UUID brandId, Pageable pageable);
}
