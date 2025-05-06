package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.TicketPurchaseDTO;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.ImageQualityService;
import com.tickefy.tickefy.service.TicketService;
import com.tickefy.tickefy.service.UserService;
import com.tickefy.tickefy.service.UserServiceImpl;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@Validated
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
                                       @RequestParam @NotBlank(message = "Home team name is required.") String homeTeamName,
                                       @RequestParam @NotBlank(message = "Away team name is required.") String awayTeamName,
                                       @RequestParam("matchDate") @NotBlank(message = "Match date is required.") String matchDateString,
                                       @RequestParam @Min(value = 1, message = "Seat number must be greater than 0.") int seatNumber,
                                       @RequestParam @NotBlank(message = "Venue name is required.") String VenueName,
                                       @RequestParam @NotBlank(message = "Venue city is required.") String VenueCity,
                                       @RequestParam @NotBlank(message = "Card type is required.") String cardType,
                                       @RequestParam @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits.") String cardNumber,
                                       @RequestParam @NotBlank(message = "Card holder name is required.") String cardHolderName,
                                       @RequestParam @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiration date must be in MM/YY format.") String expirationDate,
                                       @RequestParam @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits.") String cvvCode) {

        // Example: matchDateString = "25/04/2025T20:00"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime matchDate;
        try {
            matchDate = LocalDateTime.parse(matchDateString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Invalid match date format. Please use yyyy-MM-dd'T'HH:mm.");
        }

        try{

            String matchName = homeTeamName+" VS "+awayTeamName;

           Ticket ticket = ticketService.createPurchase(jwt,matchName, matchDate, seatNumber,VenueName,VenueCity);
            ticket.getPurchase().getClient().setPassword("");

            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new JsonResponse("Ticket Purchase Error : "+e.getMessage())
                    ,HttpStatus.BAD_REQUEST);
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
