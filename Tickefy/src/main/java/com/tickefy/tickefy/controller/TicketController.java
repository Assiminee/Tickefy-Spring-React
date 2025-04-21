package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.TicketPurchaseDTO;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.service.ImageQualityService;
import com.tickefy.tickefy.service.TicketService;
import com.tickefy.tickefy.service.UserService;
import com.tickefy.tickefy.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {


    private final UserService userService;

    private final TicketService ticketService;


    @Autowired
    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> buyTicket(@RequestHeader("Authorization") String jwt,
                                              @RequestParam("homeTeamName") String homeTeamName,
                                              @RequestParam("awayTeamName") String awayTeamName,
                                              @RequestParam("matchDate") String matchDate,
                                              @RequestParam("seatNumber") int seatNumber,
                                              @RequestParam("VenueName") String VenueName,
                                              @RequestParam("VenueCity") String VenueCity,
                                              @RequestParam("cardType") String cardType,
                                              @RequestParam("cardNumber") String cardNumber,
                                              @RequestParam("cardHolderName") String cardHolderName,
                                              @RequestParam("expirationDate") String expirationDate,
                                              @RequestParam("cvvCode") String cvvCode) {
        try{

            String matchName = homeTeamName+" VS "+awayTeamName;

            // Get the logged-in user
            Client loggedUser = (Client) userService.getProfile(jwt);


           Ticket ticket = ticketService.createPurchase(jwt,matchName, matchDate, seatNumber,VenueName,VenueCity,
                    cardType,cardNumber,cardHolderName,expirationDate,cvvCode);

            ticket.getPurchase().getClient().setPassword("");

            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
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

        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@RequestHeader("Authorization") String jwt,
                                                      @PathVariable UUID ticketId) {

        Ticket ticket = ticketService.getTicketById(jwt, ticketId);
        ticket.getPurchase().getClient().setPassword("");

        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }
}
