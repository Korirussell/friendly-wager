package com.korirussell.friendly.wager.ship;

import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ships")
public class ShipController {
    private final ShipRepository shipRepository;
    private final PirateRepository pirateRepository;
    private final ShipMemberRepository shipMemberRepository;

    public ShipController(ShipRepository shipRepository, PirateRepository pirateRepository, ShipMemberRepository shipMemberRepository) {
        this.shipRepository = shipRepository;
        this.pirateRepository = pirateRepository;
        this.shipMemberRepository = shipMemberRepository;
    }

    public record CreateShipRequest(@NotBlank String name, String description, Boolean isPrivate) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(Authentication auth, @Valid @RequestBody CreateShipRequest req) {
        Long pirateId = Long.parseLong((String) auth.getPrincipal());
        Pirate creator = pirateRepository.findById(pirateId).orElseThrow();
        Ship ship = new Ship();
        ship.setName(req.name());
        ship.setDescription(req.description());
        ship.setPrivateShip(req.isPrivate() == null ? true : req.isPrivate());
        ship.setCreatedBy(creator);
        ship = shipRepository.save(ship);

        ShipMember member = new ShipMember();
        member.setShip(ship);
        member.setPirate(creator);
        member.setRole("CAPTAIN");
        member.setBootyBalance(ship.getStartingBalance());
        shipMemberRepository.save(member);

        return ResponseEntity.ok(ship.getId());
    }

    @GetMapping
    public List<Ship> myShips(Authentication auth) {
        Long pirateId = Long.parseLong((String) auth.getPrincipal());
        return shipMemberRepository.findAllShipsForPirate(pirateId);
    }
}

