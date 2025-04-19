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

@RestController
@RequestMapping("/api/tickets")
public class TicketController {


    private final UserService userService;

    private final ClientRepository clientRepository;

    private final TicketService ticketService;

    private final ImageQualityService imageQualityService;

    @Autowired
    public TicketController(TicketService ticketService, UserService userService,
                            ClientRepository clientRepository,ImageQualityService imageQualityService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.clientRepository = clientRepository;
        this.imageQualityService = imageQualityService;
    }

    @PostMapping
    public ResponseEntity<?> buyTicket(@RequestHeader("Authorization") String jwt,
                                              @RequestParam("homeTeamName") String homeTeamName,
                                              @RequestParam("awayTeamName") String awayTeamName,
                                              @RequestParam("matchDate") String matchDate,
                                              @RequestParam("seatNumber") int seatNumber,
                                              @RequestParam("VenueName") String VenueName,
                                              @RequestParam("VenueCity") String VenueCity,
                                              @RequestParam(name = "facePhoto",required = false) MultipartFile facePhoto) {
        try{

            String matchName = homeTeamName+" VS "+awayTeamName;


            // Get the logged-in user
            Client loggedUser = (Client) userService.getProfile(jwt);

            Ticket ticket = null;

            if (facePhoto == null || facePhoto.isEmpty()) {

                ticket = ticketService.createPurchase(jwt,matchName, matchDate, seatNumber,VenueName,VenueCity);
                ticket.getPurchase().getClient().setPassword("");

                return new ResponseEntity<>(ticket, HttpStatus.CREATED);
            }

            boolean isImageValid = imageQualityService.assessImageQuality(loggedUser.getId(), facePhoto);

            if (!isImageValid) {
                return ResponseEntity
                        .badRequest()
                        .body("Image quality is too low for facial recognition. Please upload a clearer image.");
            }

            loggedUser.setHasImage(true);  // user gave us his image
            clientRepository.save(loggedUser);

            ticket = ticketService.createPurchase(jwt,matchName, matchDate, seatNumber,VenueName,VenueCity);
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
}
