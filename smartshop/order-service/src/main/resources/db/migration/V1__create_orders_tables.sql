-- V1__create_orders_tables.sql — Create orders and order_items tables
CREATE TABLE orders (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    correlation_id  VARCHAR(36) NOT NULL UNIQUE,  -- UUID for Saga tracking
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount    NUMERIC(12, 2) NOT NULL,
    shipping_address VARCHAR(500),
    notes           VARCHAR(1000),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE
);

ALTER TABLE orders ADD CONSTRAINT chk_orders_status
    CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'));

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_correlation_id ON orders (correlation_id);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id   BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    sku          VARCHAR(50),
    quantity     INTEGER NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(12, 2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
