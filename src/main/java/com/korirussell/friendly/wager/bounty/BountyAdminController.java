package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.ship.ShipMember;
import com.korirussell.friendly.wager.ship.ShipMemberRepository;
import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bounties/admin")
public class BountyAdminController {
    private final BountyRepository bountyRepository;
    private final PirateRepository pirateRepository;
    private final ShipMemberRepository shipMemberRepository;
    private final BountyService bountyService;

    public BountyAdminController(BountyRepository bountyRepository, PirateRepository pirateRepository, ShipMemberRepository shipMemberRepository, BountyService bountyService) {
        this.bountyRepository = bountyRepository;
        this.pirateRepository = pirateRepository;
        this.shipMemberRepository = shipMemberRepository;
        this.bountyService = bountyService;
    }

    public record ResolveRequest(@NotNull Long bountyId, @NotNull String resolution) {}

    @PostMapping("/resolve")
    @Transactional
    public ResponseEntity<?> resolve(Authentication auth, @Valid @RequestBody ResolveRequest req) {
        if (!req.resolution().equals("YES") && !req.resolution().equals("NO")) {
            return ResponseEntity.badRequest().body("resolution must be YES or NO");
        }
        Long resolverId = Long.parseLong((String) auth.getPrincipal());
        Pirate resolver = pirateRepository.findById(resolverId).orElseThrow();
        Bounty bounty = bountyRepository.findById(req.bountyId()).orElseThrow();

        // Only target pirate or ship captain/officer may resolve
        boolean authorized = false;
        if (bounty.getTargetPirate() != null && bounty.getTargetPirate().getId().equals(resolverId)) {
            authorized = true;
        } else {
            ShipMember member = shipMemberRepository.findByShipIdAndPirateId(bounty.getShip().getId(), resolverId);
            if (member != null && (member.getRole().equals("CAPTAIN") || member.getRole().equals("OFFICER"))) {
                authorized = true;
            }
        }
        if (!authorized) return ResponseEntity.status(403).body("not authorized to resolve");

        bountyService.resolve(bounty, resolver, req.resolution());
        return ResponseEntity.ok().build();
    }
}

