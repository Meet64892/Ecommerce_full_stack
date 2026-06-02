package com.smartshop.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Inventory - Product stock record entity.
 *
 * <h2>Purpose</h2>
 * Stores available and reserved units per product. A @Version field provides optimistic locking
 * to detect write conflicts across multiple service instances.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Optimistic locking: detects concurrent updates without holding DB row locks.</li>
 *   <li>Race condition prevention: protects against overselling under concurrent reservations.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * InventoryService reads and updates this entity during reservation workflows.
 *
 * @see com.smartshop.inventory.repository.InventoryRepository
 * @author SmartShop Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Version
    @Column(nullable = false)
    private Long version;
}
