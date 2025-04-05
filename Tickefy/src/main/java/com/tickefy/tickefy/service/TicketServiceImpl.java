package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.*;
import com.tickefy.tickefy.repository.PurchaseRepository;
import com.tickefy.tickefy.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
    public Purchase createPurchase(String jwt, String matchName, String matchDate, int seatNumber,
                                   String venueName, String venueCity) {

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

        return purchase;
    }

    @Override
    public List<Ticket> getClientTickets(String jwt) {

        // Get the logged-in client from JWT
        Client client = (Client) userService.getProfile(jwt);

        // Fetch tickets where the purchase belongs to the logged-in client
        return ticketRepository.findByPurchase_Client(client);
    }
}
