package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.Ticket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TicketService {

    public Purchase createPurchase(String jwt, String matchName, String matchDate, int seatNumber,
                                   String venueName, String venueCity);

    public List<Ticket> getClientTickets(String jwt);

}
