package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.CartItem;
import com.tickefy.tickefy.entities.Seat;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.CartItemRepository;
import com.tickefy.tickefy.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    private final SeatRepository seatRepository;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, SeatRepository seatRepository) {
        this.cartItemRepository = cartItemRepository;
        this.seatRepository = seatRepository;
    }


    @Override
    public CartItem addToCart(UUID clientId, int seatNumber, String stadiumName,
                              String stadiumCity, LocalDateTime matchDate) {

        CartItem cartItem = new CartItem(clientId,seatNumber,stadiumName,stadiumCity,
                matchDate,false,LocalDateTime.now());

        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getClientCart(UUID clientId) {
        return cartItemRepository.findByClientId(clientId);
    }

    @Override
    public boolean isSeatAvailable(int seatNumber, String stadiumName, String stadiumCity, LocalDateTime matchDate) {

        // Check if already purchased (seat marked as occupied)
        Optional<Seat> occupiedSeat = seatRepository.findOccupiedSeat(seatNumber, stadiumName, stadiumCity);
        if (occupiedSeat.isPresent()) {
            System.out.println("Seat is already Purchased : " + true);
            return true;
        }

        System.out.println("Seat is Reserved in a cart : "+ !cartItemRepository.existsBySeatNumberAndStadiumNameAndStadiumCityAndMatchDateAndReservedFalse(
                seatNumber, stadiumName, stadiumCity, matchDate));
        return !cartItemRepository.existsBySeatNumberAndStadiumNameAndStadiumCityAndMatchDateAndReservedFalse(
                seatNumber, stadiumName, stadiumCity, matchDate);
    }

    @Override
    public void removeCartItem(UUID clientId, UUID itemId) {

        CartItem cartItem = cartItemRepository.findByIdAndClientId(itemId,clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found"));
        cartItemRepository.delete(cartItem);
    }
}
