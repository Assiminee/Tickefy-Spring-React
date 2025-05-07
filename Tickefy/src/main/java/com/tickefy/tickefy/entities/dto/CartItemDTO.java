package com.tickefy.tickefy.entities.dto;


import java.util.UUID;

public class CartItemDTO {

    private UUID cartItemId;

    private String matchName;

    private String matchDate;

    private int seatNumber;

    private String VenueName;

    private String venueCity;

    public CartItemDTO() {
    }

    public CartItemDTO(UUID cartItemId, String matchName, String matchDate, int seatNumber, String venueName, String venueCity) {
        this.cartItemId = cartItemId;
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.seatNumber = seatNumber;
        VenueName = venueName;
        this.venueCity = venueCity;
    }

    public CartItemDTO(String matchName, String matchDate, int seatNumber, String venueName, String venueCity) {
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.seatNumber = seatNumber;
        VenueName = venueName;
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
        return VenueName;
    }

    public void setVenueName(String venueName) {
        VenueName = venueName;
    }

    public String getVenueCity() {
        return venueCity;
    }

    public void setVenueCity(String venueCity) {
        this.venueCity = venueCity;
    }
}
