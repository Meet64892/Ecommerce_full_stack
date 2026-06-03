package com.smartshop.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem - Line item within an Order aggregate.
 *
 * <h2>Purpose</h2>
 * Line items preserve the product id, quantity, and price at checkout time. Storing unit price on the order protects
 * historical totals even if product prices change later.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Many-to-one: Many items belong to one order.</li>
 *   <li>Snapshot pricing: Order history should not depend on mutable catalog prices.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Order owns items and publishes their product/quantity data in OrderCreatedEvent for inventory reservation.
 *
 * @see Order
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @Column(name = "brand_id")
    private UUID brandId;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Creates an order item snapshot.
     *
     * @param productId referenced product id
     * @param quantity ordered quantity
     * @param unitPrice price captured at checkout
     */
    public OrderItem(UUID productId, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
