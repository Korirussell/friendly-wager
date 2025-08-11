package com.korirussell.friendly.wager.vote;

import com.korirussell.friendly.wager.bounty.Bounty;
import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;

@Entity
@Table(name = "bounty_votes", uniqueConstraints = @UniqueConstraint(columnNames = {"bounty_id","pirate_id"}))
public class BountyVote extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bounty_id")
    private Bounty bounty;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pirate_id")
    private Pirate pirate;

    @Column(nullable = false)
    private String vote; // UP or DOWN

    public Long getId() { return id; }
    public Bounty getBounty() { return bounty; }
    public void setBounty(Bounty bounty) { this.bounty = bounty; }
    public Pirate getPirate() { return pirate; }
    public void setPirate(Pirate pirate) { this.pirate = pirate; }
    public String getVote() { return vote; }
    public void setVote(String vote) { this.vote = vote; }
}

