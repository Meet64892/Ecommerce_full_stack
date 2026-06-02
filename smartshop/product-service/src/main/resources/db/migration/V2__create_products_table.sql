CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    rating INTEGER NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id)
);
