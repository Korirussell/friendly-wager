package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.ship.Ship;
import com.korirussell.friendly.wager.ship.ShipRepository;
import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bounties")
public class BountyController {
    private final BountyRepository bountyRepository;
    private final ShipRepository shipRepository;
    private final PirateRepository pirateRepository;
    private final BountyConfirmationRepository confirmationRepository;

    public BountyController(BountyRepository bountyRepository, ShipRepository shipRepository, PirateRepository pirateRepository, BountyConfirmationRepository confirmationRepository) {
        this.bountyRepository = bountyRepository;
        this.shipRepository = shipRepository;
        this.pirateRepository = pirateRepository;
        this.confirmationRepository = confirmationRepository;
    }

    public record CreateBountyRequest(@NotNull Long shipId, @NotBlank String title, String description, @NotNull OffsetDateTime deadline, Long targetPirateId, Long secondConfirmerId) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(Authentication auth, @Valid @RequestBody CreateBountyRequest req) {
        Long pirateId = Long.parseLong((String) auth.getPrincipal());
        Pirate creator = pirateRepository.findById(pirateId).orElseThrow();
        Ship ship = shipRepository.findById(req.shipId()).orElseThrow();

        Bounty b = new Bounty();
        b.setShip(ship);
        b.setCreatedBy(creator);
        if (req.targetPirateId() != null) pirateRepository.findById(req.targetPirateId()).ifPresent(b::setTargetPirate);
        if (req.secondConfirmerId() != null) {
            pirateRepository.findById(req.secondConfirmerId()).ifPresent(p -> {
                // placeholder: second confirmer stored via confirmations table
            });
        }
        b.setTitle(req.title());
        b.setDescription(req.description());
        b.setDeadline(req.deadline());
        final Bounty savedBounty = bountyRepository.save(b);

        // create pending confirmation placeholders
        if (savedBounty.getTargetPirate() != null) {
            BountyConfirmation targetConf = new BountyConfirmation();
            targetConf.setBounty(savedBounty);
            targetConf.setConfirmer(savedBounty.getTargetPirate());
            targetConf.setRole("TARGET");
            confirmationRepository.save(targetConf);
        }
        final Long secondId = req.secondConfirmerId();
        if (secondId != null) {
            pirateRepository.findById(secondId).ifPresent(second -> {
                BountyConfirmation sc = new BountyConfirmation();
                sc.setBounty(savedBounty);
                sc.setConfirmer(second);
                sc.setRole("SECONDARY");
                confirmationRepository.save(sc);
            });
        }
        return ResponseEntity.ok(savedBounty.getId());
    }

    @GetMapping("/ship/{shipId}")
    public List<Bounty> feed(@PathVariable Long shipId) {
        // simple feed: all bounties by ship for now
        return bountyRepository.findAll().stream().filter(b -> b.getShip().getId().equals(shipId)).toList();
    }
}

