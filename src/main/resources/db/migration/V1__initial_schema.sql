-- PostgreSQL initial schema for Friendly Wager
-- Enums
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'friendship_status') THEN
        CREATE TYPE friendship_status AS ENUM ('PENDING','ACCEPTED','REJECTED','BLOCKED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'member_role') THEN
        CREATE TYPE member_role AS ENUM ('CAPTAIN','OFFICER','MATE');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'bounty_status') THEN
        CREATE TYPE bounty_status AS ENUM ('OPEN','LOCKED','RESOLVED','CANCELLED','EXPIRED');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'bounty_resolution') THEN
        CREATE TYPE bounty_resolution AS ENUM ('YES','NO','UNKNOWN');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'wager_choice') THEN
        CREATE TYPE wager_choice AS ENUM ('YES','NO');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'vote_type') THEN
        CREATE TYPE vote_type AS ENUM ('UP','DOWN');
    END IF;
END $$;

-- Core tables
CREATE TABLE IF NOT EXISTS pirates (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    avatar_url VARCHAR(512),
    email_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ships (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_private BOOLEAN NOT NULL DEFAULT TRUE,
    created_by_pirate_id BIGINT NOT NULL REFERENCES pirates(id),
    starting_balance INT NOT NULL DEFAULT 1000,
    max_active_wagers_per_member INT NOT NULL DEFAULT 10,
    allowance_per_period INT NOT NULL DEFAULT 1000,
    allowance_period VARCHAR(10) NOT NULL DEFAULT 'MONTH',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ship_members (
    id BIGSERIAL PRIMARY KEY,
    ship_id BIGINT NOT NULL REFERENCES ships(id) ON DELETE CASCADE,
    pirate_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    role member_role NOT NULL DEFAULT 'MATE',
    booty_balance INT NOT NULL DEFAULT 1000,
    active_wager_count INT NOT NULL DEFAULT 0,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (ship_id, pirate_id)
);

CREATE TABLE IF NOT EXISTS bounties (
    id BIGSERIAL PRIMARY KEY,
    ship_id BIGINT NOT NULL REFERENCES ships(id) ON DELETE CASCADE,
    created_by_pirate_id BIGINT NOT NULL REFERENCES pirates(id),
    target_pirate_id BIGINT REFERENCES pirates(id),
    title VARCHAR(140) NOT NULL,
    description TEXT,
    deadline TIMESTAMPTZ NOT NULL,
    status bounty_status NOT NULL DEFAULT 'OPEN',
    resolution bounty_resolution,
    resolved_at TIMESTAMPTZ,
    resolved_by_pirate_id BIGINT REFERENCES pirates(id),
    locked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_bounties_ship_status ON bounties (ship_id, status);
CREATE INDEX IF NOT EXISTS idx_bounties_deadline ON bounties (deadline);

CREATE TABLE IF NOT EXISTS wagers (
    id BIGSERIAL PRIMARY KEY,
    bounty_id BIGINT NOT NULL REFERENCES bounties(id) ON DELETE CASCADE,
    pirate_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    amount INT NOT NULL CHECK (amount > 0),
    choice wager_choice NOT NULL,
    is_correct BOOLEAN,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (bounty_id, pirate_id)
);
CREATE INDEX IF NOT EXISTS idx_wagers_pirate ON wagers (pirate_id);

CREATE TABLE IF NOT EXISTS bounty_votes (
    id BIGSERIAL PRIMARY KEY,
    bounty_id BIGINT NOT NULL REFERENCES bounties(id) ON DELETE CASCADE,
    pirate_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    vote vote_type NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (bounty_id, pirate_id)
);
CREATE INDEX IF NOT EXISTS idx_bounty_votes_bounty ON bounty_votes (bounty_id);

CREATE TABLE IF NOT EXISTS crewmates (
    id BIGSERIAL PRIMARY KEY,
    pirate_one_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    pirate_two_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    requested_by_id BIGINT NOT NULL REFERENCES pirates(id) ON DELETE CASCADE,
    status friendship_status NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    responded_at TIMESTAMPTZ,
    UNIQUE (pirate_one_id, pirate_two_id)
);

-- Ensure ordered pair uniqueness rule: pirate_one_id < pirate_two_id
CREATE OR REPLACE FUNCTION enforce_crewmate_order() RETURNS TRIGGER AS $$
DECLARE
    temp BIGINT;
BEGIN
    IF NEW.pirate_one_id = NEW.pirate_two_id THEN
        RAISE EXCEPTION 'pirate_one_id and pirate_two_id cannot be the same';
    END IF;
    IF NEW.pirate_one_id > NEW.pirate_two_id THEN
        -- swap
        temp := NEW.pirate_one_id;
        NEW.pirate_one_id := NEW.pirate_two_id;
        NEW.pirate_two_id := temp;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_crewmates_order ON crewmates;
CREATE TRIGGER trg_crewmates_order
BEFORE INSERT OR UPDATE ON crewmates
FOR EACH ROW EXECUTE FUNCTION enforce_crewmate_order();

