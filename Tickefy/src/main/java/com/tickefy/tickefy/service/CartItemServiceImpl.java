package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.CartItem;
import com.tickefy.tickefy.entities.Seat;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.CartItemRepository;
import com.tickefy.tickefy.repository.SeatRepository;
import com.tickefy.tickefy.repository.TicketRepository;
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

    private final TicketRepository ticketRepository;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository, SeatRepository seatRepository,
                               TicketRepository ticketRepository) {
        this.cartItemRepository = cartItemRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }


    @Override
    public CartItem addToCart(UUID clientId, String matchName, int seatNumber, String stadiumName,
                              String stadiumCity, LocalDateTime matchDate) {

        CartItem cartItem = new CartItem(clientId,matchName, seatNumber,stadiumName,stadiumCity,
                matchDate);

        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> getClientCart(UUID clientId) {
        return cartItemRepository.findByClientId(clientId);
    }

    @Override
    public CartItem getCartItemById(UUID clientId, UUID itemId) {

        return cartItemRepository.findByIdAndClientId(itemId,clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found"));
    }

    @Override
    public boolean isSeatAvailable(int seatNumber, String stadiumName, String stadiumCity, LocalDateTime matchDate) {

        // Check if already purchased (seat marked as occupied)
        Optional<Ticket> occupiedSeat = ticketRepository.findBySeatAndStadiumAndMatchDate(seatNumber,stadiumName,stadiumCity,matchDate);
        if (occupiedSeat.isPresent()) {
            System.out.println("Seat number "+seatNumber+" is already Purchased : " + true);
            return true;
        }
        System.out.println("Seat number "+seatNumber+" is available" );
        return false;
    }

    @Override
    public void removeCartItem(UUID clientId, UUID itemId) {

        CartItem cartItem = cartItemRepository.findByIdAndClientId(itemId,clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found"));
        cartItemRepository.delete(cartItem);
    }
}
