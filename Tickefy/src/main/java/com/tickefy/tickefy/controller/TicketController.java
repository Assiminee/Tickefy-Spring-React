package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.TicketPurchaseDTO;
import com.tickefy.tickefy.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {


    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Purchase> buyTicket(@RequestHeader("Authorization") String jwt,
                                              @RequestBody TicketPurchaseDTO purchaseDTO) {
        try{

            String matchName = purchaseDTO.getHomeTeamName()+" VS "+purchaseDTO.getAwayTeamName();
            int seatNumber = purchaseDTO.getSeatNumber();
            double price = purchaseDTO.getPrice();

            Purchase purchase = ticketService.createPurchase(jwt,matchName, seatNumber, price);
            return new ResponseEntity<>(purchase, HttpStatus.CREATED);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getMyTickets(@RequestHeader("Authorization") String jwt) {

        List<Ticket> tickets = ticketService.getClientTickets(jwt);

        return new ResponseEntity<>(tickets, HttpStatus.CREATED);
    }
}
