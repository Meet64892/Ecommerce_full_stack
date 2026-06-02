-- V2__create_products_table.sql
CREATE TABLE products (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(200) NOT NULL,
    description    TEXT,
    sku            VARCHAR(50) NOT NULL UNIQUE,
    price          NUMERIC(12, 2) NOT NULL,
    original_price NUMERIC(12, 2),
    brand          VARCHAR(100),
    rating         DOUBLE PRECISION DEFAULT 0.0,
    rating_count   INTEGER DEFAULT 0,
    category_id    BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    image_url      VARCHAR(500),
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_products_active ON products (active);
CREATE INDEX idx_products_price ON products (price);

-- Sample products
INSERT INTO products (name, description, sku, price, brand, rating, rating_count, category_id, active)
VALUES
    ('iPhone 15 Pro', 'Apple''s flagship smartphone with titanium design', 'APPLE-IP15P-001', 999.99, 'Apple', 4.8, 1250, 6, true),
    ('MacBook Pro 14"', 'Professional laptop with M3 Pro chip', 'APPLE-MBP14-001', 1999.99, 'Apple', 4.9, 450, 7, true),
    ('Sony WH-1000XM5', 'Industry-leading noise canceling headphones', 'SONY-WH1000XM5-001', 349.99, 'Sony', 4.7, 890, 8, true);
