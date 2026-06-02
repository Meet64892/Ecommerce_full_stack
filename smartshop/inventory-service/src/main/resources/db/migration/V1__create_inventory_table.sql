CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL,
    reserved_quantity INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

INSERT INTO inventory (product_id, available_quantity, reserved_quantity, version)
VALUES (1, 100, 0, 0);
