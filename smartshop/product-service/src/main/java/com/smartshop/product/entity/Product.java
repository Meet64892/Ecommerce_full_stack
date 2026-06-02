package com.smartshop.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

/**
 * Product - Dual-persistence domain object (PostgreSQL row + Elasticsearch doc).
 *
 * <h2>Purpose</h2>
 * Represents a catalog item. It is the authoritative relational entity AND the
 * shape we index into Elasticsearch for search. This single class is annotated
 * for both stores to teach the dual-write idea in one place.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Why two stores</b>: PostgreSQL excels at transactional, relational
 *       queries (joins, constraints, ACID). Elasticsearch excels at full-text
 *       search, fuzzy matching, and faceting via its <i>inverted index</i>.
 *       Using each for what it is best at gives correctness AND fast search.</li>
 *   <li><b>@Document</b>: marks the class as an Elasticsearch document and names
 *       the index. <b>@Field</b> declares how a property is mapped/analyzed
 *       (e.g. {@code Text} fields are tokenized for full-text; {@code Keyword}
 *       fields are stored verbatim for exact filters/aggregations).</li>
 *   <li><b>Inverted index</b>: ES builds a map from each term to the documents
 *       containing it, so "find all products mentioning 'wireless'" is a fast
 *       lookup instead of a full scan. Indexes are split into <i>shards</i> for
 *       horizontal scale and <i>replicas</i> for availability.</li>
 *   <li><b>JPA annotations</b> ({@code @Entity}/{@code @Column}) are ignored by
 *       ES, and ES annotations are ignored by JPA — they coexist harmlessly.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Saved to PostgreSQL via {@code ProductRepository} and indexed into ES via
 * {@code ProductSearchRepository} (dual write in {@code ProductServiceImpl}).
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "products")
// Elasticsearch index name. ES auto-creates the index from these mappings.
@Document(indexName = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * Shared identifier. {@code @jakarta.persistence.Id} marks the JPA primary
     * key; {@code @org.springframework.data.annotation.Id} marks the ES document
     * id. Both point at the same field so the two stores stay aligned by id.
     */
    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Product name — full-text searchable (tokenized) in Elasticsearch. */
    @Column(nullable = false)
    @Field(type = FieldType.Text)
    private String name;

    /** Long description — also full-text searchable. */
    @Column(length = 2000)
    @Field(type = FieldType.Text)
    private String description;

    /**
     * Price. Stored as BigDecimal to avoid floating-point rounding errors that
     * plague money math with double/float.
     */
    @Column(nullable = false)
    @Field(type = FieldType.Double)
    private BigDecimal price;

    /**
     * Category id (denormalized). Kept as Keyword in ES so it can be used for
     * exact-match filters and aggregations (faceted navigation).
     */
    @Column(name = "category_id")
    @Field(type = FieldType.Keyword)
    private Long categoryId;

    /** Average customer rating (0..5), filterable as a numeric range in ES. */
    @Column
    @Field(type = FieldType.Double)
    private Double rating;
}
