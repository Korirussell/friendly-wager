package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.ship.ShipMember;
import com.korirussell.friendly.wager.ship.ShipMemberRepository;
import com.korirussell.friendly.wager.user.Pirate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class BountyService {
    private final BountyRepository bountyRepository;
    private final ShipMemberRepository shipMemberRepository;

    public BountyService(BountyRepository bountyRepository, ShipMemberRepository shipMemberRepository) {
        this.bountyRepository = bountyRepository;
        this.shipMemberRepository = shipMemberRepository;
    }

    @Transactional
    public Bounty resolve(Bounty bounty, Pirate resolver, String resolution) {
        bounty.setStatus("RESOLVED");
        bounty.setResolution(resolution);
        bounty.setResolvedAt(OffsetDateTime.now());
        bounty.setResolvedBy(resolver);
        // distribution will be handled in a later iteration (ledger-based)
        return bountyRepository.save(bounty);
    }
}

