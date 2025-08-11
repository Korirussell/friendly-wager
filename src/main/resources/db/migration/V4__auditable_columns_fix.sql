-- Add missing auditable columns to match JPA @MappedSuperclass Auditable

-- ship_members: add created_at, updated_at
ALTER TABLE ship_members
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE ship_members
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- wagers: ensure updated_at exists
ALTER TABLE wagers
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- bounty_votes: ensure updated_at exists
ALTER TABLE bounty_votes
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- crewmates: add created_at, updated_at
ALTER TABLE crewmates
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE crewmates
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

