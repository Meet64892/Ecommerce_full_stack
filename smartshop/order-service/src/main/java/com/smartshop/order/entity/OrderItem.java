package com.smartshop.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * OrderItem - A single line item within an {@link Order}.
 *
 * <h2>Purpose</h2>
 * Captures one product, its quantity, and the unit price at the time of purchase
 * (prices change; we snapshot what the customer actually paid).
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@ManyToOne</b>: many items belong to one order. The {@code @JoinColumn}
 *       names the foreign-key column on this side.</li>
 *   <li>Price is snapshotted here rather than referenced live from the catalog,
 *       so historical orders remain accurate even after a price change.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Owned by {@link Order} via a one-to-many relationship.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The owning order. LAZY-loaded by default for @ManyToOne is EAGER; kept simple. */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Catalog product id (cross-service reference, not a DB FK). */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** How many units of the product were ordered. */
    @Column(nullable = false)
    private int quantity;

    /** Unit price captured at purchase time. */
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
