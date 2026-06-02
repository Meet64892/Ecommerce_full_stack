-- =============================================================================
-- V2__create_products_table.sql — products table (relational source of truth)
-- =============================================================================
-- PostgreSQL is authoritative; Elasticsearch holds a derived copy for search.
-- =============================================================================

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    -- NUMERIC(12,2) stores money exactly (no binary floating-point rounding).
    price       NUMERIC(12, 2) NOT NULL,
    category_id BIGINT,
    rating      DOUBLE PRECISION,
    -- Logical foreign key to categories. ON DELETE SET NULL keeps products even
    -- if their category is removed (they simply become uncategorized).
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

-- Index the most common filter column so category browsing stays fast as the
-- catalog grows (the relational counterpart to ES facets).
CREATE INDEX idx_products_category_id ON products (category_id);
