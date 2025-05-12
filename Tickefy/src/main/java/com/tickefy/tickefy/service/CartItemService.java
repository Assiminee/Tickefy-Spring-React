package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.CartItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public interface CartItemService {

    CartItem addToCart(UUID clientId, String homeTeamName, String awayTeamName,String homeTeamLogo,String awayTeamLogo,
                       int seatNumber, String stadiumName,
                       String stadiumCity, LocalDateTime matchDate);

    List<CartItem> getClientCart(UUID clientId);

    CartItem getCartItemById(UUID clientId, UUID itemId);

    boolean isSeatAvailable(int seatNumber, String stadiumName, String stadiumCity,
                            LocalDateTime matchDate);

    void removeCartItem(UUID clientId, UUID itemId);
}
