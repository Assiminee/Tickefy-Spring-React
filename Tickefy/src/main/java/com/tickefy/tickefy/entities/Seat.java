package com.tickefy.tickefy.entities;



import com.tickefy.tickefy.entities.enums.SeatType;
import jakarta.persistence.*;

import java.util.UUID;




@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int seatNumber;

    private String gate;

//    @Enumerated(EnumType.STRING)
//    private SeatType seatType;

    private double price;

    private boolean occupied;

    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    public Seat() {
    }

    public Seat(UUID id, int seatNumber, String gate, double price, boolean occupied, Stadium stadium) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.gate = gate;
        this.price = price;
        this.occupied = occupied;
        this.stadium = stadium;
    }

    public Seat(int seatNumber, String gate, double price, boolean occupied, Stadium stadium) {
        this.seatNumber = seatNumber;
        this.gate = gate;
        this.price = price;
        this.occupied = occupied;
        this.stadium = stadium;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }
}
