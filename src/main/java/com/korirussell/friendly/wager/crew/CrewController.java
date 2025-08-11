package com.korirussell.friendly.wager.crew;

import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/crew")
public class CrewController {
    private final CrewmateRepository crewmateRepository;
    private final PirateRepository pirateRepository;

    public CrewController(CrewmateRepository crewmateRepository, PirateRepository pirateRepository) {
        this.crewmateRepository = crewmateRepository;
        this.pirateRepository = pirateRepository;
    }

    public record RequestCrew(@NotNull Long pirateId) {}
    public record RespondCrew(@NotNull Long requestId, @NotNull String action) {}

    @PostMapping("/request")
    @Transactional
    public ResponseEntity<?> request(Authentication auth, @Valid @RequestBody RequestCrew req) {
        Long me = Long.parseLong((String) auth.getPrincipal());
        Pirate requester = pirateRepository.findById(me).orElseThrow();
        Pirate other = pirateRepository.findById(req.pirateId()).orElseThrow();
        Crewmate cm = new Crewmate();
        cm.setRequestedBy(requester);
        if (requester.getId() < other.getId()) {
            cm.setPirateOne(requester); cm.setPirateTwo(other);
        } else { cm.setPirateOne(other); cm.setPirateTwo(requester); }
        cm.setStatus("PENDING");
        crewmateRepository.save(cm);
        return ResponseEntity.ok(Map.of("id", cm.getId()));
    }

    @PostMapping("/respond")
    @Transactional
    public ResponseEntity<?> respond(Authentication auth, @Valid @RequestBody RespondCrew req) {
        Long me = Long.parseLong((String) auth.getPrincipal());
        Crewmate cm = crewmateRepository.findById(req.requestId()).orElseThrow();
        boolean involved = cm.getPirateOne().getId().equals(me) || cm.getPirateTwo().getId().equals(me);
        if (!involved) return ResponseEntity.status(403).build();
        if (req.action().equalsIgnoreCase("accept")) cm.setStatus("ACCEPTED");
        else if (req.action().equalsIgnoreCase("reject")) cm.setStatus("REJECTED");
        else return ResponseEntity.badRequest().body("action must be accept or reject");
        crewmateRepository.save(cm);
        return ResponseEntity.ok().build();
    }
}

