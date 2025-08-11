package com.korirussell.friendly.wager.ship;

import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;

@Entity
@Table(name = "ships")
public class Ship extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(name = "is_private", nullable = false)
    private boolean privateShip = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_pirate_id")
    private Pirate createdBy;

    @Column(name = "starting_balance", nullable = false)
    private int startingBalance = 1000;

    @Column(name = "max_active_wagers_per_member", nullable = false)
    private int maxActiveWagersPerMember = 10;

    @Column(name = "allowance_per_period", nullable = false)
    private int allowancePerPeriod = 1000;

    @Column(name = "allowance_period", nullable = false)
    private String allowancePeriod = "MONTH";

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isPrivateShip() { return privateShip; }
    public void setPrivateShip(boolean privateShip) { this.privateShip = privateShip; }
    public Pirate getCreatedBy() { return createdBy; }
    public void setCreatedBy(Pirate createdBy) { this.createdBy = createdBy; }
    public int getStartingBalance() { return startingBalance; }
    public void setStartingBalance(int startingBalance) { this.startingBalance = startingBalance; }
    public int getMaxActiveWagersPerMember() { return maxActiveWagersPerMember; }
    public void setMaxActiveWagersPerMember(int v) { this.maxActiveWagersPerMember = v; }
    public int getAllowancePerPeriod() { return allowancePerPeriod; }
    public void setAllowancePerPeriod(int allowancePerPeriod) { this.allowancePerPeriod = allowancePerPeriod; }
    public String getAllowancePeriod() { return allowancePeriod; }
    public void setAllowancePeriod(String allowancePeriod) { this.allowancePeriod = allowancePeriod; }
}

