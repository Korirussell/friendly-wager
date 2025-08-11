package com.korirussell.friendly.wager.bounty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BountyConfirmationRepository extends JpaRepository<BountyConfirmation, Long> {
    Optional<BountyConfirmation> findByBountyIdAndRole(Long bountyId, String role);
}

