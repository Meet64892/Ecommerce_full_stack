package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product - Catalog item stored in PostgreSQL and indexed in Elasticsearch.
 *
 * <h2>Purpose</h2>
 * PostgreSQL is excellent for transactional relational data, while Elasticsearch is optimized for full-text search
 * through inverted indexes. This entity is intentionally annotated for both systems to demonstrate a dual-write model.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Document: Declares the Elasticsearch index name that stores searchable product documents.</li>
 *   <li>@Field: Controls how values are indexed, such as text analysis for names and keyword exact matching for SKU.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductService saves it to PostgreSQL and ProductSearchRepository indexes the same object for search endpoints.
 *
 * @see com.smartshop.product.repository.ProductRepository
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/settings.json") // Optional
@EntityListeners(AuditingEntityListener.class)  // Add this line
public class Product {
    @Id
    @GeneratedValue
    private UUID id;

    @Field(type = FieldType.Text)
    @Column(nullable = false, length = 200)
    private String name;

    @Field(type = FieldType.Text)
    @Column(nullable = false, length = 2000)
    private String description;

    @Field(type = FieldType.Double)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    @Column(nullable = false, unique = true, length = 80)
    private String stockKeepingUnit;

    @Field(type = FieldType.Double)
    @Column(nullable = false)
    private double rating = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Optional: Add last modified timestamp too
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Creates a product with required catalog fields.
     *
     * @param name searchable product name
     * @param description searchable product description
     * @param price current selling price
     * @param stockKeepingUnit unique SKU for integrations
     * @param category owning category
     */
    public Product(String name, String description, BigDecimal price, String stockKeepingUnit, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockKeepingUnit = stockKeepingUnit;
        this.category = category;
    }
}
