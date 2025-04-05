package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.TicketPurchaseDTO;
import com.tickefy.tickefy.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> buyTicket(@RequestHeader("Authorization") String jwt,
                                              @RequestParam("homeTeamName") String homeTeamName,
                                              @RequestParam("awayTeamName") String awayTeamName,
                                              @RequestParam("matchDate") String matchDate,
                                              @RequestParam("seatNumber") int seatNumber,
                                              @RequestParam("VenueName") String VenueName,
                                              @RequestParam("VenueCity") String VenueCity,
                                              @RequestParam(name = "facePhoto",required = false) MultipartFile facePhoto) {
        try{

            String matchName = homeTeamName+" VS "+awayTeamName;
            MultipartFile photo = facePhoto; //holding the facePicture for now

            Purchase purchase = ticketService.createPurchase(jwt,matchName, matchDate, seatNumber,VenueName,VenueCity);
            return new ResponseEntity<>("Ticket Purchased Successfully", HttpStatus.CREATED);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Ticket Purchase Error",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getMyTickets(@RequestHeader("Authorization") String jwt) {

        List<Ticket> tickets = ticketService.getClientTickets(jwt);
        for (Ticket ticket : tickets) {
           ticket.getPurchase().getClient().setPassword("");
        }

        return new ResponseEntity<>(tickets, HttpStatus.CREATED);
    }
}
