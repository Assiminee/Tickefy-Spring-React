package com.tickefy.stadium_seats.repository;


import com.tickefy.stadium_seats.entities.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, UUID> {
}
