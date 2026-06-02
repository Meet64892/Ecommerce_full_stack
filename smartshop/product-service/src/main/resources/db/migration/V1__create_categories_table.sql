-- =============================================================================
-- V1__create_categories_table.sql — categories reference table
-- =============================================================================
-- Created first because products reference a category id (logical FK). Keeping
-- it as the earliest migration guarantees the lookup data exists before product
-- rows that point at it.
-- =============================================================================

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    -- Unique so we never get two "Electronics" categories.
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);
