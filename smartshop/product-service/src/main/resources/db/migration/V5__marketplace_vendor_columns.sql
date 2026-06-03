ALTER TABLE products ADD COLUMN brand_id UUID;
ALTER TABLE products ADD COLUMN approval_status VARCHAR(30) NOT NULL DEFAULT 'APPROVED';

UPDATE products SET approval_status = 'APPROVED' WHERE approval_status IS NULL;

CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_approval ON products(approval_status);
