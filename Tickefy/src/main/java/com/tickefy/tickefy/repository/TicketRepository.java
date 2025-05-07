package com.tickefy.tickefy.repository;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByPurchase_Client(Client client);

    Optional<Ticket> findByPurchase_Client_IdAndMatchDate(UUID clientId, LocalDate matchDate);

    Optional<Ticket> findFirstByPurchase_Client_IdOrderByMatchDateAsc(UUID clientId);

    @Query("""
    SELECT t FROM Ticket t
    WHERE t.seat.seatNumber = :seatNumber
      AND t.seat.stadium.name = :stadiumName
      AND t.seat.stadium.city = :stadiumCity
      AND t.matchDate = :matchDate
""")
    Optional<Ticket> findBySeatAndStadiumAndMatchDate(
            @Param("seatNumber") int seatNumber,
            @Param("stadiumName") String stadiumName,
            @Param("stadiumCity") String stadiumCity,
            @Param("matchDate") LocalDateTime matchDate
    );

}
