-- Migrate legacy role names to the new three-tier model.
UPDATE users SET role = 'USER' WHERE role = 'CUSTOMER';
UPDATE users SET role = 'SUPER_ADMIN' WHERE role = 'ADMIN';
UPDATE users SET role = 'SUPER_USER' WHERE role = 'SELLER';

DELETE FROM user_roles_reference;

INSERT INTO user_roles_reference(role, description) VALUES
('USER', 'Customer who browses and purchases products'),
('SUPER_USER', 'Company or brand owner who manages their product catalog'),
('SUPER_ADMIN', 'Platform administrator with full system access');
