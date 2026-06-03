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
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/settings.json")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "product_analyzer")
    @Column(nullable = false, length = 200)
    private String name;

    @Field(type = FieldType.Text, analyzer = "product_analyzer")
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

    /** Flattened for Elasticsearch; populated before indexing. */
    @Transient
    @Field(type = FieldType.Keyword)
    private UUID categoryId;

    /** Flattened for Elasticsearch; populated before indexing. */
    @Transient
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product(String name, String description, BigDecimal price, String stockKeepingUnit, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockKeepingUnit = stockKeepingUnit;
        this.category = category;
    }
}
