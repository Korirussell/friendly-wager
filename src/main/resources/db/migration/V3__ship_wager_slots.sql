-- Enforce per-ship per-target wager slot: one bet per member per target per open bounty
-- For simplicity, we check at application layer; this migration adds helpful indexes.

CREATE INDEX IF NOT EXISTS idx_bounties_ship_target_status ON bounties (ship_id, target_pirate_id, status);
CREATE INDEX IF NOT EXISTS idx_wagers_bounty_pirate ON wagers (bounty_id, pirate_id);

