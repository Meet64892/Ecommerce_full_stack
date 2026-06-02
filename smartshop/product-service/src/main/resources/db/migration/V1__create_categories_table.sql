-- V1__create_categories_table.sql
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    slug        VARCHAR(100) NOT NULL UNIQUE,
    parent_id   BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_categories_slug ON categories (slug);
CREATE INDEX idx_categories_parent ON categories (parent_id);

-- Seed data: top-level categories
INSERT INTO categories (name, slug, description) VALUES
    ('Electronics', 'electronics', 'Electronic devices and accessories'),
    ('Clothing', 'clothing', 'Fashion and apparel'),
    ('Books', 'books', 'Books, e-books, and educational materials'),
    ('Home & Garden', 'home-garden', 'Home furniture, decor, and garden supplies'),
    ('Sports & Outdoors', 'sports-outdoors', 'Sports equipment and outdoor gear');

-- Sub-categories
INSERT INTO categories (name, slug, description, parent_id) VALUES
    ('Mobile Phones', 'mobile-phones', 'Smartphones and accessories', 1),
    ('Laptops', 'laptops', 'Laptops and notebooks', 1),
    ('Headphones', 'headphones', 'Headphones and earbuds', 1);
