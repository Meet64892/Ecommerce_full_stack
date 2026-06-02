-- V1__create_inventory_table.sql
CREATE TABLE inventory (
    id                   BIGSERIAL PRIMARY KEY,
    product_id           BIGINT NOT NULL UNIQUE,
    sku                  VARCHAR(50) NOT NULL,
    quantity             INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reserved_quantity    INTEGER NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    low_stock_threshold  INTEGER DEFAULT 10,
    version              BIGINT NOT NULL DEFAULT 0   -- Optimistic locking version
);

CREATE UNIQUE INDEX idx_inventory_product_id ON inventory (product_id);
CREATE INDEX idx_inventory_sku ON inventory (sku);

-- Sample inventory data
INSERT INTO inventory (product_id, sku, quantity, reserved_quantity, low_stock_threshold)
VALUES
    (1, 'APPLE-IP15P-001', 100, 0, 10),
    (2, 'APPLE-MBP14-001', 50, 0, 5),
    (3, 'SONY-WH1000XM5-001', 75, 0, 10);
