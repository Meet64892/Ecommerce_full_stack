-- =============================================================================
-- 01-create-databases.sql — create one database per service on first init
-- =============================================================================
-- PostgreSQL runs every *.sql in /docker-entrypoint-initdb.d ONCE, the first
-- time the data volume is initialized. We give each microservice its own logical
-- database so schemas stay isolated (a service can only touch its own data),
-- while sharing a single container for dev simplicity.
--
-- These names match the JDBC URLs in each service's application.yml:
--   smartshop_users, smartshop_products, smartshop_orders, smartshop_inventory
-- =============================================================================

CREATE DATABASE smartshop_users;
CREATE DATABASE smartshop_products;
CREATE DATABASE smartshop_orders;
CREATE DATABASE smartshop_inventory;

-- The smartshop superuser (POSTGRES_USER) already owns these since it ran the
-- CREATE statements, so each service can connect with the same dev credentials.
