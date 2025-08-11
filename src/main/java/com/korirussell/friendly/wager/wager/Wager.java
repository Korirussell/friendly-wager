package com.korirussell.friendly.wager.wager;

import com.korirussell.friendly.wager.bounty.Bounty;
import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;

@Entity
@Table(name = "wagers", uniqueConstraints = @UniqueConstraint(columnNames = {"bounty_id","pirate_id"}))
public class Wager extends Auditable {

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
    private int amount;

    @Column(nullable = false)
    private String choice; // YES or NO

    @Column(name = "is_correct")
    private Boolean correct;

    public Long getId() { return id; }
    public Bounty getBounty() { return bounty; }
    public void setBounty(Bounty bounty) { this.bounty = bounty; }
    public Pirate getPirate() { return pirate; }
    public void setPirate(Pirate pirate) { this.pirate = pirate; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getChoice() { return choice; }
    public void setChoice(String choice) { this.choice = choice; }
    public Boolean getCorrect() { return correct; }
    public void setCorrect(Boolean correct) { this.correct = correct; }
}

