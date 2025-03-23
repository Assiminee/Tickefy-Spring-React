package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
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


    private UserService userService;

    private TicketRepository ticketRepository;

    private PurchaseRepository purchaseRepository;

    @Autowired
    public TicketServiceImpl(UserService userService, TicketRepository ticketRepository,PurchaseRepository purchaseRepository) {
        this.userService = userService;
        this.ticketRepository = ticketRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Purchase createPurchase(String jwt, String matchName, int seatEndpoint, double price) {

        Client client = (Client) userService.getProfile(jwt);

        // Create a new ticket
        Ticket ticket = new Ticket();

        ticket.setMatchName(matchName);
        ticket.setSeatNumber(seatEndpoint);
        ticket.setQrCode(UUID.randomUUID().toString()); // Generate unique QR code

        // Create a new purchase
        Purchase purchase = new Purchase();

        purchase.setClient(client);
        purchase.setAmount(1); // Single ticket purchase
        purchase.setTotalPrice(price);
        purchase.setTickets(Collections.singletonList(ticket));

        // Set ticket purchase reference
        ticket.setPurchase(purchase);

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
