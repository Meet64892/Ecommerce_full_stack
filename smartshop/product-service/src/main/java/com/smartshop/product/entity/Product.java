package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Product - Dual-Persistence Entity (PostgreSQL JPA + Elasticsearch Document)
 *
 * <h2>Purpose</h2>
 * Represents a product in the SmartShop catalog. This class serves two roles:
 *   1. @Entity: JPA entity mapped to the "products" table in PostgreSQL
 *   2. @Document: Elasticsearch document indexed in the "products" index
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual-Write Strategy: When a product is created or updated, the service
 *       writes to BOTH PostgreSQL and Elasticsearch. This ensures:
 *       - PostgreSQL: consistent transactional storage, foreign key integrity
 *       - Elasticsearch: up-to-date search index for full-text queries
 *       Trade-off: consistency vs complexity. A failed ES write means search is
 *       temporarily stale — acceptable for product search but not for order processing.</li>
 *   <li>@Document(indexName = "products"): Maps this class to the Elasticsearch index.
 *       In Elasticsearch, an "index" is roughly equivalent to a database table.
 *       When this document is saved via ProductSearchRepository, Elasticsearch
 *       creates an inverted index entry for each @Field(type = TEXT).</li>
 *   <li>Inverted Index: Elasticsearch's secret to fast full-text search.
 *       For text "Apple iPhone 15 Pro", it creates entries:
 *       "apple" → [docId: 1, position: 0]
 *       "iphone" → [docId: 1, position: 1]
 *       "15" → [docId: 1, position: 2]
 *       "pro" → [docId: 1, position: 3]
 *       Searching "iphone pro" looks up both terms and finds the document in O(1).</li>
 *   <li>@Field(type = TEXT) vs KEYWORD:
 *       TEXT: analyzed (tokenized, stemmed, lowercased) → used for full-text search
 *       KEYWORD: not analyzed → used for exact matching, sorting, aggregations</li>
 *   <li>BigDecimal for prices: NEVER use float or double for monetary values.
 *       Floating point numbers have precision errors: 0.1 + 0.2 = 0.30000000000000004
 *       BigDecimal provides exact decimal arithmetic.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_category", columnList = "category_id"),
        @Index(name = "idx_products_sku", columnList = "sku"),
        @Index(name = "idx_products_active", columnList = "active")
    }
)
@EntityListeners(AuditingEntityListener.class)
// Elasticsearch document mapping
@Document(indexName = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Shared ID used for both JPA (@Id) and Elasticsearch (@Id).
     * The same ID identifies this product in both stores, simplifying synchronization.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Spring Data Elasticsearch uses the same @Id annotation as JPA
    @org.springframework.data.annotation.Id
    private Long id;

    /**
     * Product name — indexed in Elasticsearch as TEXT for full-text search.
     * Also stored in PostgreSQL for transactional use.
     */
    @Column(name = "name", nullable = false, length = 200)
    @Field(type = FieldType.Text, analyzer = "standard")  // "standard" analyzer tokenizes and lowercases
    private String name;

    /**
     * Full product description — heavily analyzed for search relevance.
     * "english" analyzer: tokenizes, removes stop words ("the", "a"), and applies stemming
     * ("running" → "run") so "runs" and "running" match the same query.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Field(type = FieldType.Text, analyzer = "english")
    private String description;

    /**
     * Stock Keeping Unit — unique product identifier for inventory management.
     * Stored as KEYWORD in Elasticsearch for exact matching (not analyzed).
     */
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    @Field(type = FieldType.Keyword)  // Exact match only — "ABC-123" won't split on "-"
    private String sku;

    /**
     * Price in the platform's base currency (USD).
     * BigDecimal ensures no floating-point precision loss.
     * precision=12, scale=2: up to 9,999,999,999.99 (handles enterprise product prices).
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @Field(type = FieldType.Double)   // Elasticsearch Double for numeric range queries
    private BigDecimal price;

    /**
     * Original/compare-at price for showing discounts.
     * Null if not on sale.
     */
    @Column(name = "original_price", precision = 12, scale = 2)
    @Field(type = FieldType.Double)
    private BigDecimal originalPrice;

    /**
     * Brand name — indexed as TEXT for search, KEYWORD for filtering/aggregation.
     * Using multi-field mapping would let us do both, but for simplicity we use TEXT.
     */
    @Column(name = "brand", length = 100)
    @Field(type = FieldType.Text)
    private String brand;

    /** Average customer rating (0.0 - 5.0) */
    @Column(name = "rating")
    @Field(type = FieldType.Double)
    private Double rating;

    /** Number of customer ratings (for confidence in rating score) */
    @Column(name = "rating_count")
    @Field(type = FieldType.Integer)
    @Builder.Default
    private Integer ratingCount = 0;

    /**
     * Category reference (PostgreSQL foreign key).
     * FetchType.LAZY: category data is NOT loaded by default (loaded only when accessed).
     * This prevents unnecessary JOIN queries when you only need the product data.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Category ID stored separately for Elasticsearch indexing.
     * Since Elasticsearch doesn't do JOINs, we denormalize the category ID
     * so we can filter by category in search queries.
     */
    @Column(name = "category_id", insertable = false, updatable = false)
    @Field(type = FieldType.Long)
    private Long categoryId;

    /**
     * Category name denormalized for Elasticsearch search.
     * Without this, a search for products by category name would require
     * a JOIN that Elasticsearch can't do.
     */
    @Field(type = FieldType.Keyword)
    @Transient  // Not in PostgreSQL — only in Elasticsearch (set from category.getName())
    private String categoryName;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Whether this product is visible to customers */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
