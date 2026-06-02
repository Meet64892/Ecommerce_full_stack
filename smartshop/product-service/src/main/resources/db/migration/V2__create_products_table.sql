-- Products stay normalized in PostgreSQL; Elasticsearch can be rebuilt from these rows if an index is lost.
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    stock_keeping_unit VARCHAR(80) NOT NULL UNIQUE,
    rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    category_id UUID NOT NULL REFERENCES categories(id)
);
