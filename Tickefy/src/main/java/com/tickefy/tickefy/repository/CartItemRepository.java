package com.tickefy.tickefy.repository;


import com.tickefy.tickefy.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByClientId(UUID clientId);

    boolean existsBySeatNumberAndVenueNameAndVenueCityAndMatchDate(
            int seatNumber, String stadiumName, String stadiumCity, LocalDateTime matchDate);

    Optional<CartItem> findByIdAndClientId(UUID id, UUID clientId);
}
