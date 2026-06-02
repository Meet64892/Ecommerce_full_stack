-- =============================================================================
-- V2__add_roles.sql — Add Profile Photo URL Column to Users
-- =============================================================================
--
-- This migration demonstrates how Flyway handles schema evolution:
-- We ADD a column (safe, backward compatible) rather than changing existing ones.
-- Adding columns is safe because old code that doesn't reference the new column
-- continues to work unchanged (existing SELECT * queries return the new column,
-- but existing entity mappings just ignore unmapped columns).
--
-- NEVER do in production:
-- - Rename a column (breaks all code referencing the old name)
-- - Delete a column (breaks all code referencing it)
-- Instead: add a new column → migrate data → update code → (much later) drop old column
-- =============================================================================

-- Add profile photo URL column (nullable — existing users don't have a photo)
ALTER TABLE users ADD COLUMN profile_photo_url VARCHAR(500);

-- Add phone number column for future 2FA/SMS notifications
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);

-- Add index on phone_number for future 2FA lookups
CREATE INDEX idx_users_phone ON users (phone_number) WHERE phone_number IS NOT NULL;

-- Add a "last_login_at" column for security monitoring and session management
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP WITH TIME ZONE;
