-- =============================================================================
-- postgres-init.sql — Initialize SmartShop Databases
-- =============================================================================
-- This script runs ONCE when the PostgreSQL container is first created.
-- It creates a separate database for each microservice.
-- Each service uses its OWN database, enforcing the microservice principle:
-- "each service owns its data and nobody else can access it directly".
-- =============================================================================

-- User Service Database
CREATE DATABASE smartshop_users;
GRANT ALL PRIVILEGES ON DATABASE smartshop_users TO smartshop;

-- Product Service Database
CREATE DATABASE smartshop_products;
GRANT ALL PRIVILEGES ON DATABASE smartshop_products TO smartshop;

-- Order Service Database
CREATE DATABASE smartshop_orders;
GRANT ALL PRIVILEGES ON DATABASE smartshop_orders TO smartshop;

-- Inventory Service Database
CREATE DATABASE smartshop_inventory;
GRANT ALL PRIVILEGES ON DATABASE smartshop_inventory TO smartshop;
