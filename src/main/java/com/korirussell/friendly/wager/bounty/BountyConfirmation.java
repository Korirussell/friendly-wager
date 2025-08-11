package com.korirussell.friendly.wager.bounty;

import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bounty_confirmations", uniqueConstraints = @UniqueConstraint(columnNames = {"bounty_id","role"}))
public class BountyConfirmation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bounty_id")
    private Bounty bounty;

    @ManyToOne(optional = false)
    @JoinColumn(name = "confirmer_pirate_id")
    private Pirate confirmer;

    @Column(nullable = false)
    private String role; // TARGET or SECONDARY

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "evidence_url")
    private String evidenceUrl;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    public Long getId() { return id; }
    public Bounty getBounty() { return bounty; }
    public void setBounty(Bounty bounty) { this.bounty = bounty; }
    public Pirate getConfirmer() { return confirmer; }
    public void setConfirmer(Pirate confirmer) { this.confirmer = confirmer; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEvidenceUrl() { return evidenceUrl; }
    public void setEvidenceUrl(String evidenceUrl) { this.evidenceUrl = evidenceUrl; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public OffsetDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(OffsetDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
}

