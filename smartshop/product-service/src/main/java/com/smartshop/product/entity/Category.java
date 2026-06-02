package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Category - Product category reference entity.
 *
 * <h2>Purpose</h2>
 * Categories group products for filtering and merchandising. Keeping categories normalized avoids
 * denormalized category text inconsistencies across products.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Normalization: avoids duplicate category names and update anomalies.</li>
 *   <li>Reference data: stable taxonomy for search filters.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Products reference this entity via foreign key relation.
 *
 * @see Product
 * @author SmartShop Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;
}
