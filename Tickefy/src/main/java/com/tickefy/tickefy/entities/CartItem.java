package com.tickefy.tickefy.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;



@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID clientId;

    private int seatNumber;

    private String stadiumName;

    private String stadiumCity;

    private LocalDateTime matchDate;

    private boolean reserved;

    private LocalDateTime addedAt;

    public CartItem() {}

    public CartItem(UUID id, UUID clientId, int seatNumber, String stadiumName, String stadiumCity, LocalDateTime matchDate, boolean reserved, LocalDateTime addedAt) {
        this.id = id;
        this.clientId = clientId;
        this.seatNumber = seatNumber;
        this.stadiumName = stadiumName;
        this.stadiumCity = stadiumCity;
        this.matchDate = matchDate;
        this.reserved = reserved;
        this.addedAt = addedAt;
    }

    public CartItem(UUID clientId, int seatNumber, String stadiumName, String stadiumCity, LocalDateTime matchDate, boolean reserved, LocalDateTime addedAt) {
        this.clientId = clientId;
        this.seatNumber = seatNumber;
        this.stadiumName = stadiumName;
        this.stadiumCity = stadiumCity;
        this.matchDate = matchDate;
        this.reserved = reserved;
        this.addedAt = addedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getStadiumCity() {
        return stadiumCity;
    }

    public void setStadiumCity(String stadiumCity) {
        this.stadiumCity = stadiumCity;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
