package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.dto.CartItemDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TicketService {

    public Purchase createPurchase(String jwt, List<CartItemDTO> cartItemDTOS);

    public List<Ticket> getClientTickets(String jwt);

    public Ticket getTicketById(String jwt, UUID ticketId);

    public Optional<Ticket> findTodayTicketByClient(UUID clientId);

    public Optional<Ticket> findTodayWithTimeTicketByClient(UUID clientId);

}
