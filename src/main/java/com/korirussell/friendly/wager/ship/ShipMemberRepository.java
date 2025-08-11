package com.korirussell.friendly.wager.ship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipMemberRepository extends JpaRepository<ShipMember, Long> {
    @Query("select sm.ship from ShipMember sm where sm.pirate.id = :pirateId")
    List<Ship> findAllShipsForPirate(@Param("pirateId") Long pirateId);

    @Query("select sm from ShipMember sm where sm.ship.id = :shipId and sm.pirate.id = :pirateId")
    ShipMember findByShipIdAndPirateId(@Param("shipId") Long shipId, @Param("pirateId") Long pirateId);

    @Query("select sm from ShipMember sm where sm.ship.id = :shipId order by sm.bootyBalance desc")
    List<ShipMember> leaderboardForShip(@Param("shipId") Long shipId);
}

