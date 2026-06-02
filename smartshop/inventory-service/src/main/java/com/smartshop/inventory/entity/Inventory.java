package com.smartshop.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Inventory - Stock record for a single product.
 *
 * <h2>Purpose</h2>
 * Holds the available quantity per product and the version used for optimistic
 * locking during concurrent reservations.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@Version (optimistic locking)</b>: each update checks the version did
 *       not change since we read it; if another transaction updated the row
 *       first, JPA throws {@code OptimisticLockException}
 *       (StaleObjectStateException underneath) and we retry. This prevents the
 *       classic <i>race condition</i> where two orders both read "5 in stock"
 *       and both decrement to 4, overselling.</li>
 *   <li><b>Optimistic vs pessimistic</b>: optimistic assumes conflicts are rare
 *       and detects them at write time (no DB locks held; great for high read,
 *       low contention). Pessimistic ({@code SELECT ... FOR UPDATE}) locks the
 *       row up front, serializing writers — use it only under heavy contention
 *       where retries would be wasteful.</li>
 *   <li><b>Serializable</b>: implemented so instances can be cached in Redis.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Persisted by {@code InventoryRepository}; reserved by {@code InventoryServiceImpl}.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory implements Serializable {

    /**
     * Primary key = the product id (one stock row per product). We reuse the
     * product id directly rather than a surrogate key since it is naturally
     * unique and is how callers look stock up.
     */
    @Id
    @Column(name = "product_id")
    private Long productId;

    /** Units currently available to reserve. */
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    /**
     * Optimistic-lock version. JPA increments it on each update and includes the
     * prior value in the UPDATE's WHERE clause to detect concurrent writes.
     */
    @Version
    @Column(nullable = false)
    private long version;
}
