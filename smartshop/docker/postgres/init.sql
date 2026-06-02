-- PostgreSQL runs this script once when the data volume is empty.
-- Separate databases preserve service ownership of data even when one local server hosts them all.
CREATE DATABASE smartshop_users;
CREATE DATABASE smartshop_products;
CREATE DATABASE smartshop_orders;
CREATE DATABASE smartshop_inventory;
