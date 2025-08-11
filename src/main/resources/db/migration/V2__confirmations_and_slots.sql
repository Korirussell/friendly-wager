-- Add second confirmer and confirmation workflow
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'confirmation_role') THEN
        CREATE TYPE confirmation_role AS ENUM ('TARGET','SECONDARY');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'confirmation_status') THEN
        CREATE TYPE confirmation_status AS ENUM ('PENDING','APPROVED','REJECTED');
    END IF;
END $$;

ALTER TABLE bounties ADD COLUMN IF NOT EXISTS second_confirmer_id BIGINT REFERENCES pirates(id);

CREATE TABLE IF NOT EXISTS bounty_confirmations (
    id BIGSERIAL PRIMARY KEY,
    bounty_id BIGINT NOT NULL REFERENCES bounties(id) ON DELETE CASCADE,
    confirmer_pirate_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    role confirmation_role NOT NULL,
    status confirmation_status NOT NULL DEFAULT 'PENDING',
    evidence_url VARCHAR(1024),
    note TEXT,
    confirmed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (bounty_id, role)
);

CREATE INDEX IF NOT EXISTS idx_bounty_confirmations_bounty ON bounty_confirmations (bounty_id);

