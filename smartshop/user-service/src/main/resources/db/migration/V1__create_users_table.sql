-- =============================================================================
-- V1__create_users_table.sql — initial users schema (Flyway migration)
-- =============================================================================
-- WHY FLYWAY: migrations are versioned, ordered, and applied automatically at
-- startup. The file name encodes the version (V1, V2, ...). Flyway records which
-- migrations have run in its schema_history table, so every environment converges
-- to the exact same schema. We NEVER ALTER tables by hand in prod — every change
-- is a new, reviewable, replayable migration file.
-- =============================================================================

CREATE TABLE users (
    -- BIGSERIAL = auto-incrementing 64-bit PK, matching GenerationType.IDENTITY.
    id            BIGSERIAL PRIMARY KEY,
    -- Unique login identifier; the UNIQUE constraint is the ultimate guard
    -- against duplicate accounts even under race conditions.
    email         VARCHAR(255) NOT NULL UNIQUE,
    -- BCrypt hash (~60 chars). Column is generously sized for future algorithms.
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    -- Auditing columns auto-populated by Spring Data JPA auditing.
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(100)
);

-- Index to speed up the very common "find by email" login lookup. (UNIQUE
-- already creates one on email; this comment documents the access pattern.)
