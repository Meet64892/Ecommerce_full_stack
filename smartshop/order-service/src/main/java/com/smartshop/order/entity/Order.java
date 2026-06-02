package com.smartshop.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - Aggregate root for a customer's purchase.
 *
 * <h2>Purpose</h2>
 * Holds the order header (owner, status, total) and its line items. The Saga
 * mutates {@code status} as the distributed flow progresses.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Aggregate</b>: Order + its OrderItems form one consistency boundary;
 *       items are persisted/removed together via cascade + orphan removal.</li>
 *   <li><b>Auditing</b>: created/updated timestamps are auto-populated.</li>
 *   <li><b>@Enumerated(STRING)</b>: persists the status name, not the ordinal,
 *       so reordering the enum can't corrupt existing data.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Managed by {@code OrderRepository}; created by {@code OrderServiceImpl} and
 * advanced by the saga as inventory events arrive.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The customer who placed the order (user-service id). */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Current lifecycle state, advanced by the saga. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /** Sum of line items; computed at creation. */
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    /**
     * Line items. {@code cascade = ALL} + {@code orphanRemoval} means saving the
     * order saves its items, and removing an item from this list deletes it.
     * {@code mappedBy = "order"} marks OrderItem as the FK-owning side.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Adds an item and keeps both sides of the relationship in sync (critical
     * for cascade persistence to work correctly).
     *
     * @param item the line item to attach to this order
     */
    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }
}
