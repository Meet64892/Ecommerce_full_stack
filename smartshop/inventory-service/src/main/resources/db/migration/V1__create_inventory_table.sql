-- The version column powers optimistic locking and prevents lost updates during concurrent reservations.
CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    quantity_available INTEGER NOT NULL,
    quantity_reserved INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);
