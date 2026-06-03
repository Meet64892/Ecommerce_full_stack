-- Add created_at column to products table
ALTER TABLE products
ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW();

-- Add updated_at column to products table
ALTER TABLE products
ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Create an index on created_at for better query performance
CREATE INDEX idx_products_created_at ON products(created_at);