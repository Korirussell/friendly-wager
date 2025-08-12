-- Switch enum-typed columns to VARCHAR to align with JPA String mappings

-- ship_members.role member_role -> varchar
ALTER TABLE ship_members
  ALTER COLUMN role TYPE varchar USING role::text;

-- bounties.status bounty_status -> varchar
ALTER TABLE bounties
  ALTER COLUMN status TYPE varchar USING status::text;

-- bounties.resolution bounty_resolution -> varchar
ALTER TABLE bounties
  ALTER COLUMN resolution TYPE varchar USING resolution::text;

-- wagers.choice wager_choice -> varchar
ALTER TABLE wagers
  ALTER COLUMN choice TYPE varchar USING choice::text;

-- bounty_votes.vote vote_type -> varchar
ALTER TABLE bounty_votes
  ALTER COLUMN vote TYPE varchar USING vote::text;

-- crewmates.status friendship_status -> varchar
ALTER TABLE crewmates
  ALTER COLUMN status TYPE varchar USING status::text;

-- bounty_confirmations.role confirmation_role -> varchar
ALTER TABLE bounty_confirmations
  ALTER COLUMN role TYPE varchar USING role::text;

-- bounty_confirmations.status confirmation_status -> varchar
ALTER TABLE bounty_confirmations
  ALTER COLUMN status TYPE varchar USING status::text;

