package com.korirussell.friendly.wager.crew;

import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;

@Entity
@Table(name = "crewmates", uniqueConstraints = @UniqueConstraint(columnNames = {"pirate_one_id","pirate_two_id"}))
public class Crewmate extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pirate_one_id")
    private Pirate pirateOne;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pirate_two_id")
    private Pirate pirateTwo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_id")
    private Pirate requestedBy;

    @Column(nullable = false)
    private String status = "PENDING";

    public Long getId() { return id; }
    public Pirate getPirateOne() { return pirateOne; }
    public void setPirateOne(Pirate pirateOne) { this.pirateOne = pirateOne; }
    public Pirate getPirateTwo() { return pirateTwo; }
    public void setPirateTwo(Pirate pirateTwo) { this.pirateTwo = pirateTwo; }
    public Pirate getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Pirate requestedBy) { this.requestedBy = requestedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

