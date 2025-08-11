package com.korirussell.friendly.wager.vote;

import com.korirussell.friendly.wager.bounty.Bounty;
import com.korirussell.friendly.wager.bounty.BountyRepository;
import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final BountyVoteRepository bountyVoteRepository;
    private final BountyRepository bountyRepository;
    private final PirateRepository pirateRepository;

    public VoteController(BountyVoteRepository bountyVoteRepository, BountyRepository bountyRepository, PirateRepository pirateRepository) {
        this.bountyVoteRepository = bountyVoteRepository;
        this.bountyRepository = bountyRepository;
        this.pirateRepository = pirateRepository;
    }

    public record VoteRequest(@NotNull Long bountyId, @NotNull String vote) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> castVote(Authentication auth, @Valid @RequestBody VoteRequest req) {
        if (!req.vote().equals("UP") && !req.vote().equals("DOWN")) {
            return ResponseEntity.badRequest().body("vote must be UP or DOWN");
        }
        Long pirateId = Long.parseLong((String) auth.getPrincipal());
        Pirate pirate = pirateRepository.findById(pirateId).orElseThrow();
        Bounty bounty = bountyRepository.findById(req.bountyId()).orElseThrow();

        BountyVote vote = bountyVoteRepository.findAll().stream()
                .filter(v -> v.getBounty().getId().equals(bounty.getId()) && v.getPirate().getId().equals(pirateId))
                .findFirst().orElse(null);
        if (vote == null) {
            vote = new BountyVote();
            vote.setBounty(bounty);
            vote.setPirate(pirate);
        }
        vote.setVote(req.vote());
        bountyVoteRepository.save(vote);
        return ResponseEntity.ok().build();
    }
}

