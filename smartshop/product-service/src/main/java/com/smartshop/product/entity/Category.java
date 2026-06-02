package com.smartshop.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Category - JPA entity for product categories.
 *
 * <h2>Purpose</h2>
 * A simple lookup table used to group products (e.g. "Electronics"). Kept
 * relational only — categories don't need full-text search.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reference data: small, slowly-changing rows products point to by id.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Managed via {@code CategoryController}; products store the category id.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique human-readable category name. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Optional longer description shown in the UI. */
    @Column(length = 500)
    private String description;
}
