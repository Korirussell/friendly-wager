package com.korirussell.friendly.wager.ship;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ships/{shipId}/leaderboard")
public class LeaderboardController {
    private final ShipMemberRepository shipMemberRepository;

    public LeaderboardController(ShipMemberRepository shipMemberRepository) {
        this.shipMemberRepository = shipMemberRepository;
    }

    @GetMapping
    public List<ShipMember> leaderboard(@PathVariable Long shipId) {
        return shipMemberRepository.leaderboardForShip(shipId);
    }
}

