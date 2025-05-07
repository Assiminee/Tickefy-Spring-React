package com.tickefy.tickefy.entities.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CartItemDTO {

    @NotNull(message = "Cart item ID is required.")
    private UUID cartItemId;

    @NotBlank(message = "Match name is required.")
    private String matchName;

    @NotBlank(message = "Match date is required. Format: yyyy-MM-dd'T'HH:mm")
    private String matchDate;

    @Min(value = 1, message = "Seat number must be greater than 0.")
    private int seatNumber;

    @NotBlank(message = "Venue name is required.")
    private String venueName;

    @NotBlank(message = "Venue city is required.")
    private String venueCity;


    public CartItemDTO() {
    }

    public CartItemDTO(UUID cartItemId, String matchName, String matchDate, int seatNumber, String venueName, String venueCity) {
        this.cartItemId = cartItemId;
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.seatNumber = seatNumber;
        this.venueName = venueName;
        this.venueCity = venueCity;
    }

    public UUID getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }
}
