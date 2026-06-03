CREATE TABLE brands (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(2000),
    logo_url VARCHAR(500),
    owner_user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(30) NOT NULL,
    commission_percent NUMERIC(5, 2) NOT NULL DEFAULT 10.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_brands_owner ON brands(owner_user_id);
CREATE INDEX idx_brands_status ON brands(status);

ALTER TABLE users ADD COLUMN brand_id UUID REFERENCES brands(id);
