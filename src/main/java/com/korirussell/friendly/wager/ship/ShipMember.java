package com.korirussell.friendly.wager.ship;

import com.korirussell.friendly.wager.common.Auditable;
import com.korirussell.friendly.wager.user.Pirate;
import jakarta.persistence.*;

@Entity
@Table(name = "ship_members", uniqueConstraints = @UniqueConstraint(columnNames = {"ship_id", "pirate_id"}))
public class ShipMember extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ship_id")
    private Ship ship;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pirate_id")
    private Pirate pirate;

    @Column(nullable = false)
    private String role = "MATE";

    @Column(name = "booty_balance", nullable = false)
    private int bootyBalance = 1000;

    @Column(name = "active_wager_count", nullable = false)
    private int activeWagerCount = 0;

    public Long getId() { return id; }
    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }
    public Pirate getPirate() { return pirate; }
    public void setPirate(Pirate pirate) { this.pirate = pirate; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getBootyBalance() { return bootyBalance; }
    public void setBootyBalance(int bootyBalance) { this.bootyBalance = bootyBalance; }
    public int getActiveWagerCount() { return activeWagerCount; }
    public void setActiveWagerCount(int activeWagerCount) { this.activeWagerCount = activeWagerCount; }
}

