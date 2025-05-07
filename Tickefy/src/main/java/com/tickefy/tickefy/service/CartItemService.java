package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.CartItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public interface CartItemService {

    CartItem addToCart(UUID clientId, int seatNumber, String stadiumName,
                       String stadiumCity, LocalDateTime matchDate);

    List<CartItem> getClientCart(UUID clientId);

    boolean isSeatAvailable(int seatNumber, String stadiumName, String stadiumCity,
                            LocalDateTime matchDate);

    void removeCartItem(UUID clientId, UUID itemId);
}
