package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.ship.Ship;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bounties")
public class Bounty extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_pirate_id")
    private Pirate createdBy;

    @ManyToOne
    @JoinColumn(name = "target_pirate_id")
    private Pirate targetPirate;

    @Column(length = 140, nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private OffsetDateTime deadline;

    @Column(nullable = false)
    private String status = "OPEN";

    @Column
    private String resolution; // YES|NO|UNKNOWN

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "resolved_by_pirate_id")
    private Pirate resolvedBy;

    @Column(name = "locked_at")
    private OffsetDateTime lockedAt;

    public Long getId() { return id; }
    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }
    public Pirate getCreatedBy() { return createdBy; }
    public void setCreatedBy(Pirate createdBy) { this.createdBy = createdBy; }
    public Pirate getTargetPirate() { return targetPirate; }
    public void setTargetPirate(Pirate targetPirate) { this.targetPirate = targetPirate; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getDeadline() { return deadline; }
    public void setDeadline(OffsetDateTime deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public Pirate getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(Pirate resolvedBy) { this.resolvedBy = resolvedBy; }
    public OffsetDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(OffsetDateTime lockedAt) { this.lockedAt = lockedAt; }
}

