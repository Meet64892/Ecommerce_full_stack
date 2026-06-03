-- PostgreSQL runs this script once when the data volume is empty.
-- Separate databases preserve service ownership of data even when one local server hosts them all.
CREATE DATABASE smartshop_users;
CREATE DATABASE smartshop_products;
CREATE DATABASE smartshop_orders;
CREATE DATABASE smartshop_inventory;

-- Legacy configs used username "postgres"; Docker creates role "smartshop" only.
CREATE USER postgres WITH PASSWORD 'smartshop' SUPERUSER;
GRANT ALL PRIVILEGES ON DATABASE smartshop_users TO postgres;
GRANT ALL PRIVILEGES ON DATABASE smartshop_products TO postgres;
GRANT ALL PRIVILEGES ON DATABASE smartshop_orders TO postgres;
GRANT ALL PRIVILEGES ON DATABASE smartshop_inventory TO postgres;
