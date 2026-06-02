-- =============================================================================
-- V1__create_inventory_table.sql — per-product stock table
-- =============================================================================
-- One row per product. The version column powers JPA optimistic locking so
-- concurrent reservations cannot oversell.
-- =============================================================================

CREATE TABLE inventory (
    -- product_id is the natural PK (one stock record per product).
    product_id         BIGINT PRIMARY KEY,
    -- Available units; CHECK guards against ever going negative even if app
    -- logic had a bug (defense in depth).
    available_quantity INT NOT NULL CHECK (available_quantity >= 0),
    -- Optimistic-locking version, incremented by Hibernate on each update.
    version            BIGINT NOT NULL DEFAULT 0
);

-- Seed a little demo stock so the saga has something to reserve out of the box.
INSERT INTO inventory (product_id, available_quantity, version) VALUES
    (1, 100, 0),
    (2, 50, 0),
    (3, 0, 0);  -- product 3 is out of stock to demonstrate the CANCELLED path.
