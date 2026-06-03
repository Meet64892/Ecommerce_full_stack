CREATE TABLE brands (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    owner_user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    commission_percent NUMERIC(5, 2) NOT NULL DEFAULT 10.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_brands_status ON brands(status);
CREATE INDEX idx_brands_owner ON brands(owner_user_id);

ALTER TABLE users ADD COLUMN brand_id UUID REFERENCES brands(id);
