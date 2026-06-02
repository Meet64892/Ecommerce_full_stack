package com.smartshop.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Inventory - JPA Entity Representing Product Stock Levels
 *
 * <h2>Purpose</h2>
 * Tracks available stock, reserved stock, and total stock for each product.
 * Uses optimistic locking (@Version) to prevent concurrent modification bugs.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Optimistic Locking (@Version):
 *       When two transactions read the same inventory record simultaneously:
 *       T1: reads inventory (version=5, stock=10), sets reserved=3
 *       T2: reads inventory (version=5, stock=10), sets reserved=3
 *       T1 commits: UPDATE inventory SET reserved=3, version=6 WHERE id=1 AND version=5 → SUCCESS
 *       T2 commits: UPDATE inventory SET reserved=3, version=6 WHERE id=1 AND version=5 → FAILS
 *         (version is now 6, not 5) → Hibernate throws OptimisticLockException
 *       T2 should retry. This prevents double-reservation without database-level locking.
 *
 *       vs Pessimistic Locking:
 *       Pessimistic: SELECT * FROM inventory WHERE id=1 FOR UPDATE (database lock held during processing)
 *       - Guarantees no conflict but BLOCKS other transactions — lowers throughput
 *       Optimistic: No lock held, conflicts detected at commit time
 *       - Higher throughput when conflicts are rare (most inventory operations)
 *       - Must handle OptimisticLockException with retry logic</li>
 *   <li>availableQuantity = quantity - reservedQuantity
 *       quantity: total physical stock
 *       reservedQuantity: stock reserved for pending orders (not yet shipped)
 *       availableQuantity: what we can accept new orders for</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Entity
@Table(
    name = "inventory",
    indexes = {
        @Index(name = "idx_inventory_product_id", columnList = "product_id", unique = true)
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The product this inventory entry tracks.
     * Each product has exactly ONE inventory record (enforced by UNIQUE index).
     */
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    /**
     * Product SKU — duplicated here for quick lookups without joining to product-service.
     * Denormalization: storing SKU avoids a cross-service call for inventory lookups.
     */
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    /**
     * Total physical quantity in the warehouse.
     * This is the "raw" stock number.
     */
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    /**
     * Quantity reserved for pending orders (awaiting shipment).
     * Allows us to show accurate "available" stock without rejecting valid pending orders.
     */
    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    /**
     * Minimum threshold that triggers a "low stock" alert.
     * When quantity falls below this, ops team is notified to reorder.
     */
    @Column(name = "low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 10;

    /**
     * @Version: The optimistic locking version field.
     * Hibernate auto-increments this on every UPDATE.
     * Concurrent modifications are detected when the WHERE version=? clause returns 0 rows.
     * DO NOT set this field manually — Hibernate manages it.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Calculates the available stock that can be reserved for new orders.
     * availableQuantity = quantity - reservedQuantity
     * This is computed, not stored (no column) to avoid sync issues.
     *
     * @return the number of units available for new orders
     */
    public int getAvailableQuantity() {
        return Math.max(0, quantity - reservedQuantity);
    }

    /**
     * Reserves stock for an order.
     * Validates that enough stock is available before reserving.
     * Called within a @Transactional method — if this throws, the transaction rolls back.
     *
     * @param amount the quantity to reserve
     * @throws IllegalStateException if not enough stock is available
     */
    public void reserve(int amount) {
        int available = getAvailableQuantity();
        if (available < amount) {
            throw new IllegalStateException(
                String.format("Insufficient stock for SKU %s: requested=%d, available=%d",
                        sku, amount, available)
            );
        }
        // We use optimistic locking here instead of synchronized because
        // database-level @Version handles concurrent requests across multiple
        // service instances (synchronized only works within one JVM).
        this.reservedQuantity += amount;
    }

    /**
     * Releases previously reserved stock (compensating transaction for cancelled orders).
     *
     * @param amount the quantity to release from reservation
     */
    public void release(int amount) {
        this.reservedQuantity = Math.max(0, reservedQuantity - amount);
    }

    /**
     * Confirms a shipment: reduces quantity and reserved quantity.
     * Called when an order moves from CONFIRMED to SHIPPED status.
     *
     * @param amount the quantity shipped
     */
    public void ship(int amount) {
        this.quantity -= amount;
        this.reservedQuantity = Math.max(0, reservedQuantity - amount);
    }
}
