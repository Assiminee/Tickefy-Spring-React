package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.*;
import com.tickefy.tickefy.entities.dto.CartItemDTO;
import com.tickefy.tickefy.entities.dto.PurchaseDTO;
import com.tickefy.tickefy.entities.enums.CardType;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.PurchaseRepository;
import com.tickefy.tickefy.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TicketServiceImpl implements TicketService {


    private final UserService userService;

    private final TicketRepository ticketRepository;

    private final PurchaseRepository purchaseRepository;

    private final StadiumService stadiumService;

    private final SeatService seatService;

    private final CartItemService cartItemService;

    @Autowired
    public TicketServiceImpl(UserService userService, TicketRepository ticketRepository,
                             PurchaseRepository purchaseRepository, StadiumService stadiumService,
                             SeatService seatService, CartItemService cartItemService) {
        this.userService = userService;
        this.ticketRepository = ticketRepository;
        this.purchaseRepository = purchaseRepository;
        this.stadiumService = stadiumService;
        this.seatService = seatService;
        this.cartItemService = cartItemService;
    }


    @Override
    public Purchase createPurchase(String jwt, List<CartItemDTO> cartItems) {

        Client client = (Client) userService.getProfile(jwt);

        Purchase purchase = new Purchase();
        purchase.setClient(client);

        List<Ticket> tickets = new ArrayList<>();
        double totalPrice = 0;

        for (CartItemDTO cartItem : cartItems) {

            LocalDateTime matchDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                matchDate = LocalDateTime.parse(cartItem.getMatchDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid match date format: " + cartItem.getMatchDate());
            }

            // check the seat availability if it slipped through the cart check
            if(cartItemService.isSeatAvailable(cartItem.getSeatNumber(), cartItem.getVenueName(),
                    cartItem.getVenueCity(),matchDate))
                throw new ConflictException("Seat number " +cartItem.getSeatNumber()+ " is already Occupied. you cant purchase this ticket");

            // Get or create stadium
            Stadium stadium = stadiumService.getOrCreateStadium(cartItem.getVenueName(), cartItem.getVenueCity());

            // Create and configure seat
            Seat seat = new Seat();
            seat.setSeatNumber(cartItem.getSeatNumber());
            seat.setStadium(stadium);
            seat.setOccupied(true);

            // Gate + price logic
            int seatNumber = cartItem.getSeatNumber();
            if (seatNumber < 1000) {
                seat.setGate("FrontGate");
                seat.setPrice(1500);
            } else if (seatNumber < 5000) {
                seat.setGate("EasternGate");
                seat.setPrice(1000);
            } else if (seatNumber < 10000) {
                seat.setGate("WesternGate");
                seat.setPrice(500);
            } else {
                seat.setGate("SouthernGate");
                seat.setPrice(300);
            }

            seatService.addSeat(seat);
            totalPrice += seat.getPrice();

            // Create ticket
            Ticket ticket = new Ticket();
            ticket.setMatchName(cartItem.getMatchName());
            ticket.setMatchDate(matchDate);
            ticket.setQrCode(UUID.randomUUID().toString()); // Generate unique QR code
            ticket.setSeat(seat);
            ticket.setPurchase(purchase);

            tickets.add(ticket);

            // delete the purchased items from the cart
            cartItemService.removeCartItem(client.getId(),cartItem.getCartItemId());
        }

        purchase.setTickets(tickets);
        purchase.setAmount(tickets.size());
        purchase.setTotalPrice(totalPrice);

        purchaseRepository.save(purchase);
        ticketRepository.saveAll(tickets);

        return purchase;
    }


    @Override
    public List<Ticket> getClientTickets(String jwt) {

        // Get the logged-in client from JWT
        Client client = (Client) userService.getProfile(jwt);

        // Fetch tickets where the purchase belongs to the logged-in client
        return ticketRepository.findByPurchase_Client(client);
    }

    @Override
    public Ticket getTicketById(String jwt, UUID ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);

        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found");
        }
        return ticket;
    }

    @Override
    public Optional<Ticket> findTodayTicketByClient(UUID clientId) {

        LocalDate today = LocalDate.now();
        return ticketRepository.findByPurchase_Client_IdAndMatchDate(clientId, today);
    }

    public Optional<Ticket> findTodayWithTimeTicketByClient(UUID clientId) {

        LocalDateTime now = LocalDateTime.now();

        // Retrieve all tickets of client with matchDate >= today's match time
        Optional<Ticket> optionalTicket = ticketRepository.findFirstByPurchase_Client_IdOrderByMatchDateAsc(clientId);

        if (optionalTicket.isEmpty()) {
            System.out.println("No ticket found for this client in this day and time");
            return Optional.empty();
        }

        Ticket ticket = optionalTicket.get();
        LocalDateTime matchDateTime = ticket.getMatchDate();

        // Define allowed entrance window (3 hours before the match until the match ends)
        LocalDateTime allowedEntryStart = matchDateTime.minusHours(3);
        LocalDateTime allowedEntryEnd = matchDateTime.plusHours(2);

        if (now.isAfter(allowedEntryStart) && now.isBefore(allowedEntryEnd)) {
            System.out.println("Client has a Ticket for this time " + now);
            return Optional.of(ticket);
        }

        System.out.println("Ticket exists but NOT valid for current time");
        return Optional.empty(); // No valid ticket for current time
    }
}
