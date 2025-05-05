package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.*;
import com.tickefy.tickefy.entities.enums.CardType;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.PurchaseRepository;
import com.tickefy.tickefy.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {


    private final UserService userService;

    private final TicketRepository ticketRepository;

    private final PurchaseRepository purchaseRepository;

    private final StadiumService stadiumService;

    private final SeatService seatService;

    @Autowired
    public TicketServiceImpl(UserService userService, TicketRepository ticketRepository,
                             PurchaseRepository purchaseRepository, StadiumService stadiumService,
                             SeatService seatService) {
        this.userService = userService;
        this.ticketRepository = ticketRepository;
        this.purchaseRepository = purchaseRepository;
        this.stadiumService = stadiumService;
        this.seatService = seatService;
    }


    @Override
    public Ticket createPurchase(String jwt, String matchName, LocalDateTime matchDate, int seatNumber,
                                 String venueName, String venueCity ) {

        Client client = (Client) userService.getProfile(jwt);


        // Get the stadium or create a new one
        Stadium stadium = stadiumService.getOrCreateStadium(venueName,venueCity);

        //Create a seat
        Seat seat = new Seat();
        seat.setSeatNumber(seatNumber);

        if(seatNumber < 1000 ) {
            seat.setGate("FrontGate");
            seat.setPrice(1500);
        } else if(seatNumber > 1000 && seatNumber < 5000) {
            seat.setGate("EasternGate");
            seat.setPrice(1000);
        } else if (seatNumber > 5000 && seatNumber < 10000) {
            seat.setGate("WesternGate");
            seat.setPrice(500);
        } else if (seatNumber > 10000) {
            seat.setGate("SouthernGate");
            seat.setPrice(300);
        }
        seat.setOccupied(true);
        seat.setStadium(stadium);
        seatService.addSeat(seat);


        // Create a new ticket
        Ticket ticket = new Ticket();
        ticket.setMatchName(matchName);
        ticket.setMatchDate(matchDate);
        ticket.setQrCode(UUID.randomUUID().toString()); // Generate unique QR code


        // Create a new purchase
        Purchase purchase = new Purchase();

        purchase.setClient(client);
        purchase.setAmount(1); // Single ticket purchase
        purchase.setTotalPrice(seat.getPrice());
        purchase.setTickets(Collections.singletonList(ticket));

        // Set ticket purchase reference
        ticket.setPurchase(purchase);

        // and set ticket seat reference
        ticket.setSeat(seat);

        // Save both purchase and ticket
        purchaseRepository.save(purchase);
        ticketRepository.save(ticket);

        return ticket;
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

        // Retrieve all tickets of client with matchDate >= today (optional: only today or future matches)
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
            return Optional.of(ticket);
        }

        System.out.println("Ticket exists but NOT valid for current time");
        return Optional.empty(); // Not valid ticket for current time
    }
}
