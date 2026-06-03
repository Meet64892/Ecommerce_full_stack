-- Snapshot vendor ownership on each line item for marketplace order queries.
ALTER TABLE order_items ADD COLUMN brand_id UUID;
ALTER TABLE order_items ADD COLUMN product_name VARCHAR(200);

CREATE INDEX idx_order_items_brand_id ON order_items(brand_id);
