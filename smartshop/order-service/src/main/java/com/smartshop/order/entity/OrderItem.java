package com.smartshop.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

/**
 * OrderItem - Line item entity within an order.
 *
 * <h2>Purpose</h2>
 * Stores denormalized purchase details (product id, quantity, unit price) to preserve historical
 * purchase facts even if product catalog data changes later.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Historical snapshot: line price is stored to avoid retroactive price drift.</li>
 *   <li>Many-to-one relation: each item belongs to one order aggregate.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Created during order submission and persisted with cascading save via Order entity.
 *
 * @see Order
 * @author SmartShop Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
