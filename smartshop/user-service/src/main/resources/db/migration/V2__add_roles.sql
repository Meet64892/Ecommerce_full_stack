-- This seed data documents valid roles for humans; the enum in Java remains the source used at runtime.
CREATE TABLE user_roles_reference (
    role VARCHAR(30) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

INSERT INTO user_roles_reference(role, description) VALUES
('CUSTOMER', 'Default shopper role'),
('ADMIN', 'Administrative role for platform operations'),
('SELLER', 'Catalog management role for merchants');
