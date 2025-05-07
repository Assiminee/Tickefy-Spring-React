package com.tickefy.tickefy.repository;


import com.tickefy.tickefy.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {

    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber AND s.stadium.name = :stadiumName " +
            "AND s.stadium.city = :stadiumCity AND s.occupied = true")
    Optional<Seat> findOccupiedSeat(int seatNumber, String stadiumName, String stadiumCity);

}
