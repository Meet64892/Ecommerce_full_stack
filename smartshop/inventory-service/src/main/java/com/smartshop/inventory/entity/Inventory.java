package com.smartshop.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Inventory - JPA entity representing stock for one product.
 *
 * <h2>Purpose</h2>
 * Stock reservation is concurrency-sensitive: two checkouts can try to reserve the last unit at the same time. The
 * @Version column lets Hibernate detect stale writes and fail one transaction instead of overselling.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Optimistic locking: Allows concurrent reads and checks version only when writing.</li>
 *   <li>Pessimistic locking: Locks rows up front and is useful for high-contention critical sections.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryServiceImpl updates this entity when API calls or Kafka events reserve stock.
 *
 * @see com.smartshop.inventory.repository.InventoryRepository
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID productId;

    @Column(nullable = false)
    private int quantityAvailable;

    @Column(nullable = false)
    private int quantityReserved;

    @Version
    @Column(nullable = false)
    private long version;

    /**
     * Creates an inventory row for a product.
     *
     * @param productId product id
     * @param quantityAvailable total stock available for reservation
     */
    public Inventory(UUID productId, int quantityAvailable) {
        this.productId = productId;
        this.quantityAvailable = quantityAvailable;
    }

    /**
     * Calculates currently available unreserved stock.
     *
     * @return quantityAvailable minus quantityReserved
     */
    public int salableQuantity() {
        return quantityAvailable - quantityReserved;
    }
}
