package com.korirussell.friendly.wager.auth;

import com.korirussell.friendly.wager.security.JwtService;
import com.korirussell.friendly.wager.user.Pirate;
import com.korirussell.friendly.wager.user.PirateRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PirateRepository pirateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(PirateRepository pirateRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.pirateRepository = pirateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public record RegisterRequest(@NotBlank String username, @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token) {}
    public record LoginRequest(@NotBlank String usernameOrEmail, @NotBlank String password) {}

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (pirateRepository.findByUsername(req.username()).isPresent() || pirateRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username or email already exists"));
        }
        Pirate p = new Pirate();
        p.setUsername(req.username());
        p.setEmail(req.email());
        p.setPasswordHash(passwordEncoder.encode(req.password()));
        pirateRepository.save(p);
        String token = jwtService.generateToken(p.getId().toString(), Map.of("username", p.getUsername()));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Pirate p = pirateRepository.findByUsername(req.usernameOrEmail())
                .or(() -> pirateRepository.findByEmail(req.usernameOrEmail()))
                .orElse(null);
        if (p == null) return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        if (!passwordEncoder.matches(req.password(), p.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }
        String token = jwtService.generateToken(p.getId().toString(), Map.of("username", p.getUsername()));
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

