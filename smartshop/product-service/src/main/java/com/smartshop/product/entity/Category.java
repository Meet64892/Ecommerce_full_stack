package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Category - JPA entity grouping related products.
 *
 * <h2>Purpose</h2>
 * Categories support browsing and filtering without duplicating category text on every product row. They also create
 * a stable identifier that search and UI layers can use for facets.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Normalization: Category data is stored once and referenced by products.</li>
 *   <li>Unique name: Prevents duplicate category labels that confuse shoppers.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Product entities reference Category through a many-to-one association and ProductSearchRequest can filter by it.
 *
 * @see Product
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)  // Add this line
public class Category {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Optional: Add last modified timestamp too
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    /**
     * Creates a category with a display name and optional description.
     *
     * @param name unique category name
     * @param description human-readable category description
     */
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
