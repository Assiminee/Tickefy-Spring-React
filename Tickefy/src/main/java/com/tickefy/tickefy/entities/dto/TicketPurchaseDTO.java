package com.tickefy.tickefy.entities.dto;



public class TicketPurchaseDTO {

    private String homeTeamName;

    private String awayTeamName;

    private int seatNumber;

    private double price;

    public TicketPurchaseDTO() {
    }

    public TicketPurchaseDTO(String homeTeamName, String awayTeamName, int seatNumber, double price) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
