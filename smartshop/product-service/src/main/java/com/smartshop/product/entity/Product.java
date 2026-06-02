package com.smartshop.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Product - Product aggregate stored in PostgreSQL and Elasticsearch.
 *
 * <h2>Purpose</h2>
 * PostgreSQL provides transactional integrity for writes while Elasticsearch provides fast
 * full-text and faceted search. This dual-write approach optimizes both correctness and query UX.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>@Document indexing: defines how product data is mapped into ES inverted indexes.</li>
 *   <li>Dual-write strategy: each update persists to DB and then refreshes ES read model.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductService writes this entity to JPA and search repository for query workloads.
 *
 * @see com.smartshop.product.repository.ProductSearchRepository
 * @author SmartShop Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Document(indexName = "products")
public class Product {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Field(type = FieldType.Text)
    private String name;

    @Column(nullable = false, length = 4000)
    @Field(type = FieldType.Text)
    private String description;

    @Column(nullable = false)
    @Field(type = FieldType.Double)
    private Double price;

    @Column(nullable = false)
    @Field(type = FieldType.Integer)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Field(type = FieldType.Long)
    private Category category;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private java.time.Instant updatedAt;
}
