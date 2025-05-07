package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.CartItemDTO;
import com.tickefy.tickefy.entities.dto.PurchaseDTO;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.ImageQualityService;
import com.tickefy.tickefy.service.TicketService;
import com.tickefy.tickefy.service.UserService;
import com.tickefy.tickefy.service.UserServiceImpl;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> buyTicketsFromCart(@RequestHeader("Authorization") String jwt,
                                                @Valid @RequestBody PurchaseDTO purchaseDTO) {
        try {
            Purchase purchase = ticketService.createPurchase(jwt, purchaseDTO.getCartItems());
            purchase.getClient().setPassword(""); // Remove sensitive info

            return new ResponseEntity<>(purchase, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Ticket purchase Error : "+ e.getMessage());
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
