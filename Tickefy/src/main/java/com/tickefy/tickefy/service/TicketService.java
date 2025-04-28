package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TicketService {

    public Ticket createPurchase(String jwt, String matchName, LocalDateTime matchDate, int seatNumber,
                                 String venueName, String venueCity);

    public List<Ticket> getClientTickets(String jwt);

    public Ticket getTicketById(String jwt, UUID ticketId);

    public Optional<Ticket> findTodayTicketByClient(UUID clientId);

    public Optional<Ticket> findTodayWithTimeTicketByClient(UUID clientId);

}
