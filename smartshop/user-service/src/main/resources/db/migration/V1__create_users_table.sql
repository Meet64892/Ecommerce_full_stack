-- =============================================================================
-- V1__create_users_table.sql — Initial Users Table Migration
-- =============================================================================
--
-- WHAT IS FLYWAY?
-- Flyway is a database migration tool. Instead of manually running ALTER TABLE
-- or CREATE TABLE statements on each environment, Flyway tracks which SQL scripts
-- have been run (in a "flyway_schema_history" table) and automatically applies
-- any new ones in VERSION ORDER (V1__, V2__, V3__...).
--
-- WHY NEVER MODIFY PRODUCTION TABLES MANUALLY?
-- 1. Version drift: Dev DB has a column that staging doesn't
-- 2. No audit trail: Who added this column? When? Why?
-- 3. Rollback nightmare: How do you undo a manual ALTER TABLE?
-- Flyway solves all of this: every schema change is a versioned, reviewed SQL file.
--
-- NAMING CONVENTION: V{version}__{description}.sql
--   V = version prefix (required)
--   1 = version number (Flyway applies these in order)
--   __ = double underscore separator
--   create_users_table = human-readable description
-- =============================================================================

-- Create the users table with all required columns
-- COMMENT ON COLUMN documents the column purpose in the DB catalog
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,  -- Auto-incrementing primary key (PostgreSQL's BIGSERIAL = BIGINT + SEQUENCE)
    username      VARCHAR(50) NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- BCrypt hash format: $2a$10$... (~60 chars, store as 255 for safety)
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    role          VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',  -- Enum stored as string, not integer
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,

    -- JPA Auditing columns (auto-populated by Spring Data)
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50)
);

-- Unique constraints: prevent duplicate emails and usernames
-- These are ENFORCED at the database level (last line of defense against race conditions)
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);

-- Check constraint: role must be one of the valid values
-- This prevents arbitrary strings from being stored in the role column
ALTER TABLE users ADD CONSTRAINT chk_users_role
    CHECK (role IN ('CUSTOMER', 'ADMIN', 'SELLER'));

-- Indexes for frequently queried columns
-- Without indexes, every login (SELECT * FROM users WHERE email = ?) does a FULL TABLE SCAN
-- An index on email makes this query O(log n) instead of O(n)
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_role ON users (role);

-- Insert an initial admin user for system bootstrap
-- Password: Admin1234! (BCrypt hash of this password with strength=10)
-- IMPORTANT: Change this password immediately in production!
INSERT INTO users (username, email, password_hash, first_name, last_name, role, enabled, created_by)
VALUES (
    'admin',
    'admin@smartshop.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- "Admin1234!"
    'System',
    'Admin',
    'ADMIN',
    TRUE,
    'flyway-migration'
);
