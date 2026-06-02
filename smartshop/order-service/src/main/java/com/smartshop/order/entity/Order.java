package com.smartshop.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - JPA Entity Representing a Customer Order
 *
 * <h2>Purpose</h2>
 * The central domain object for the order service. Tracks the entire order
 * lifecycle from placement through delivery.
 *
 * @author SmartShop Team
 */
@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_orders_user_id", columnList = "user_id"),
        @Index(name = "idx_orders_status", columnList = "status"),
        @Index(name = "idx_orders_correlation_id", columnList = "correlation_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who placed this order (references user-service's user ID) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Correlation ID for the Saga — links all events related to this order.
     * Used to trace the complete order flow across Kafka messages.
     */
    @Column(name = "correlation_id", nullable = false, unique = true)
    private String correlationId;

    /**
     * Current state in the order lifecycle.
     * Stored as STRING to be safe against enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Total price of the order (sum of all item prices).
     * Recalculated when items are added/modified.
     * BigDecimal: exact decimal arithmetic for monetary values.
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Shipping address for this order */
    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    /** Optional note from the customer */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * @OneToMany with CascadeType.ALL: saving an Order also saves/updates/deletes OrderItems.
     * orphanRemoval=true: if an OrderItem is removed from this list, it's deleted from the DB.
     * This ensures the Order fully owns its items.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Helper method to add an item and set its order reference.
     * Bidirectional relationship management: both sides must be set.
     * Without this, item.getOrder() would return null even after order.getItems().add(item).
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        // Recalculate total when items change
        recalculateTotal();
    }

    /**
     * Recalculates the total order amount from all items.
     * Called automatically when items are added.
     */
    private void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
