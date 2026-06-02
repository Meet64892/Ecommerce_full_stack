-- =============================================================================
-- V1__create_orders_tables.sql — orders + order_items schema
-- =============================================================================
-- Both tables are created in one migration because they form a single aggregate
-- (an order and its line items are persisted/removed together).
-- =============================================================================

CREATE TABLE orders (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    -- Status stored as text to match @Enumerated(EnumType.STRING).
    status       VARCHAR(20) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    -- Mirror the Java enum at the DB level for defense in depth.
    CONSTRAINT chk_orders_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

CREATE TABLE order_items (
    id         BIGSERIAL PRIMARY KEY,
    -- FK to the owning order; ON DELETE CASCADE removes items with their order.
    order_id   BIGINT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    quantity   INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL
);

-- Speed up "list this user's orders" and "items of this order" lookups.
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
