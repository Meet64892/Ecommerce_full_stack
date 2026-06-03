-- Align vendor role name with marketplace spec (SUPER_USER → ADMIN).
UPDATE users SET role = 'ADMIN' WHERE role = 'SUPER_USER';

DELETE FROM user_roles_reference;

INSERT INTO user_roles_reference(role, description) VALUES
('USER', 'Customer who browses and purchases products'),
('ADMIN', 'Brand or vendor owner who manages their catalog and orders'),
('SUPER_ADMIN', 'Platform super administrator with full system access');
