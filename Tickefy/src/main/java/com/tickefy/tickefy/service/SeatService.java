package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Seat;
import org.springframework.stereotype.Service;

@Service
public interface SeatService {

    public Seat addSeat(Seat seat);
}
