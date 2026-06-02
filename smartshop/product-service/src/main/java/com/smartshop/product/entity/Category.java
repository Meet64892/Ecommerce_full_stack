package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Category - JPA Entity for Product Categories
 *
 * <h2>Purpose</h2>
 * Represents hierarchical product categories (e.g., Electronics → Mobile Phones → Smartphones).
 * Categories are stored in PostgreSQL; products reference them via foreign key.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Slug for URL-friendly category paths (e.g., "mobile-phones").
     * URL slugs are human-readable, SEO-friendly, and stable (unlike numeric IDs).
     */
    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    /**
     * Self-referential relationship: a category can have a parent category.
     * @ManyToOne: many categories can have the same parent.
     * Null means this is a top-level category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * Child categories.
     * mappedBy = "parent": Hibernate owns the relationship on the parent field of Category.
     * cascade = PERSIST: saving a parent also saves its children.
     * @OneToMany with fetch=LAZY: children are NOT loaded until accessed (N+1 query prevention).
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
