package com.tickefy.stadium_seats.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;




@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String city;

    private String address;

    private String country;

    private int gateCount;

    @OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    public Stadium() {}

    public Stadium(UUID id, String name, String city, String address, String country, int gateCount, List<Seat> seats) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.country = country;
        this.gateCount = gateCount;
        this.seats = seats;
    }

    public Stadium(String name, String city, String address, String country, int gateCount, List<Seat> seats) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.country = country;
        this.gateCount = gateCount;
        this.seats = seats;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getGateCount() {
        return gateCount;
    }

    public void setGateCount(int gateCount) {
        this.gateCount = gateCount;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
