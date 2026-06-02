-- =============================================================================
-- V2__add_roles.sql — add the role column (second, additive migration)
-- =============================================================================
-- Demonstrates incremental schema evolution: V1 created the table; V2 adds a
-- column. Because real environments already have V1 applied with data, we add
-- the column with a DEFAULT and backfill, rather than recreating the table.
-- =============================================================================

-- Add role as text (matches @Enumerated(EnumType.STRING)). DEFAULT lets existing
-- rows get a sensible value without a NULL window.
ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER';

-- Soft enable/disable flag for accounts (suspensions, email-verification gates).
ALTER TABLE users
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- A CHECK constraint enforces that only known roles are stored, mirroring the
-- Java enum at the database level (defense in depth against bad writes).
ALTER TABLE users
    ADD CONSTRAINT chk_users_role CHECK (role IN ('CUSTOMER', 'ADMIN', 'SELLER'));
