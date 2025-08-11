package com.korirussell.friendly.wager.wager;

import com.korirussell.friendly.wager.bounty.Bounty;
import com.korirussell.friendly.wager.bounty.BountyRepository;
import com.korirussell.friendly.wager.ship.ShipMember;
import com.korirussell.friendly.wager.ship.ShipMemberRepository;
import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wagers")
public class WagerController {
    private final WagerRepository wagerRepository;
    private final BountyRepository bountyRepository;
    private final PirateRepository pirateRepository;
    private final ShipMemberRepository shipMemberRepository;

    public WagerController(WagerRepository wagerRepository, BountyRepository bountyRepository, PirateRepository pirateRepository, ShipMemberRepository shipMemberRepository) {
        this.wagerRepository = wagerRepository;
        this.bountyRepository = bountyRepository;
        this.pirateRepository = pirateRepository;
        this.shipMemberRepository = shipMemberRepository;
    }

    public record PlaceWagerRequest(@NotNull Long bountyId, @Min(1) int amount, @NotNull String choice) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> place(Authentication auth, @Valid @RequestBody PlaceWagerRequest req) {
        if (!req.choice().equals("YES") && !req.choice().equals("NO")) {
            return ResponseEntity.badRequest().body("choice must be YES or NO");
        }
        Long pirateId = Long.parseLong((String) auth.getPrincipal());
        Pirate pirate = pirateRepository.findById(pirateId).orElseThrow();
        Bounty bounty = bountyRepository.findById(req.bountyId()).orElseThrow();
        ShipMember member = shipMemberRepository.findAll().stream()
                .filter(sm -> sm.getShip().getId().equals(bounty.getShip().getId()) && sm.getPirate().getId().equals(pirateId))
                .findFirst().orElse(null);
        if (member == null) {
            return ResponseEntity.status(403).body("not a member of the ship");
        }
        if (!"OPEN".equals(bounty.getStatus())) {
            return ResponseEntity.badRequest().body("bounty not open");
        }
        if (member.getBootyBalance() < req.amount()) {
            return ResponseEntity.badRequest().body("insufficient balance");
        }
        boolean already = wagerRepository.findAll().stream()
                .anyMatch(w -> w.getBounty().getId().equals(bounty.getId()) && w.getPirate().getId().equals(pirateId));
        if (already) return ResponseEntity.badRequest().body("already wagered on this bounty");

        // Enforce one active wager per member per target within a ship at a time (slot model)
        if (bounty.getTargetPirate() != null) {
            boolean hasActiveSlot = bountyRepository.findAll().stream()
                    .filter(b -> b.getShip().getId().equals(bounty.getShip().getId()))
                    .filter(b -> "OPEN".equals(b.getStatus()))
                    .filter(b -> b.getTargetPirate() != null && b.getTargetPirate().getId().equals(bounty.getTargetPirate().getId()))
                    .anyMatch(open -> wagerRepository.findAll().stream()
                            .anyMatch(w -> w.getBounty().getId().equals(open.getId()) && w.getPirate().getId().equals(pirateId)));
            if (hasActiveSlot) {
                return ResponseEntity.badRequest().body("active wager already exists on this target in this ship");
            }
        }

        Wager w = new Wager();
        w.setBounty(bounty);
        w.setPirate(pirate);
        w.setAmount(req.amount());
        w.setChoice(req.choice());
        wagerRepository.save(w);

        member.setBootyBalance(member.getBootyBalance() - req.amount());
        member.setActiveWagerCount(member.getActiveWagerCount() + 1);
        shipMemberRepository.save(member);

        return ResponseEntity.ok(w.getId());
    }
}

