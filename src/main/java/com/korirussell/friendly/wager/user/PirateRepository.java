package com.korirussell.friendly.wager.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PirateRepository extends JpaRepository<Pirate, Long> {
    Optional<Pirate> findByUsername(String username);
    Optional<Pirate> findByEmail(String email);
}

