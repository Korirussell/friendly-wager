package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/confirmations")
public class ConfirmationController {
    private final BountyRepository bountyRepository;
    private final BountyConfirmationRepository confirmationRepository;
    private final PirateRepository pirateRepository;

    public ConfirmationController(BountyRepository bountyRepository, BountyConfirmationRepository confirmationRepository, PirateRepository pirateRepository) {
        this.bountyRepository = bountyRepository;
        this.confirmationRepository = confirmationRepository;
        this.pirateRepository = pirateRepository;
    }

    public record SubmitConfirmation(@NotNull Long bountyId, @NotNull String role, @NotNull String status, String evidenceUrl, String note) {}

    @PostMapping
    @Transactional
    public ResponseEntity<?> submit(Authentication auth, @Valid @RequestBody SubmitConfirmation req) {
        if (!(req.role().equals("TARGET") || req.role().equals("SECONDARY"))) {
            return ResponseEntity.badRequest().body("role must be TARGET or SECONDARY");
        }
        if (!(req.status().equals("APPROVED") || req.status().equals("REJECTED"))) {
            return ResponseEntity.badRequest().body("status must be APPROVED or REJECTED");
        }
        Long me = Long.parseLong((String) auth.getPrincipal());
        var bounty = bountyRepository.findById(req.bountyId()).orElseThrow();
        var conf = confirmationRepository.findByBountyIdAndRole(bounty.getId(), req.role()).orElseThrow();
        if (!conf.getConfirmer().getId().equals(me)) {
            return ResponseEntity.status(403).body("not assigned confirmer");
        }
        conf.setStatus(req.status());
        conf.setEvidenceUrl(req.evidenceUrl());
        conf.setNote(req.note());
        conf.setConfirmedAt(OffsetDateTime.now());
        confirmationRepository.save(conf);
        return ResponseEntity.ok(Map.of("status", conf.getStatus()));
    }
}

