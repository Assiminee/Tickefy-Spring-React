package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Seat;
import com.tickefy.tickefy.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatServiceImpl implements SeatService {


    private final SeatRepository seatRepository;

    @Autowired
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Seat addSeat(Seat seat) {
     return seatRepository.save(seat);

    }
}
