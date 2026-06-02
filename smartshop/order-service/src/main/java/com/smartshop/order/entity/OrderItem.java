package com.smartshop.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem - Line Item Within an Order
 *
 * <h2>Purpose</h2>
 * Represents a single product+quantity in an order.
 * An Order has many OrderItems (one-to-many relationship).
 * We store the price at order time (not current product price) to prevent
 * order history from changing when product prices change.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many order items belong to one order.
     * FetchType.LAZY: order data is not loaded when fetching order items independently.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Product ID from product-service (no FK across service boundaries) */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** Product name at time of order (denormalized to preserve order history) */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /** Product SKU at time of order */
    @Column(name = "sku", length = 50)
    private String sku;

    /** Number of units ordered */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Unit price AT THE TIME THE ORDER WAS PLACED.
     * We store this separately from the current product price so that:
     *   1. Order history is immutable
     *   2. Price changes don't retroactively affect past orders
     *   3. Customers see what they were charged, not the current price
     */
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
