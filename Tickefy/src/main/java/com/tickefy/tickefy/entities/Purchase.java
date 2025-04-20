package com.tickefy.tickefy.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tickefy.tickefy.entities.enums.CardType;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int amount; // Number of tickets purchased

    private double totalPrice;

    //@Enumerated(EnumType.STRING)
    private CardType cardType;

    private String cardNumber;

    private String cardHolderName;

    private String expirationDate;

    private String cvvCode;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Reference to the client who made the purchase

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets; // Tickets associated with this purchase




    public Purchase() {
    }

    public Purchase(UUID id, int amount, double totalPrice, CardType cardType, String cardNumber, String cardHolderName, String expirationDate, String cvvCode, Client client, List<Ticket> tickets) {
        this.id = id;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationDate = expirationDate;
        this.cvvCode = cvvCode;
        this.client = client;
        this.tickets = tickets;
    }

    public Purchase(int amount, double totalPrice, CardType cardType, String cardNumber, String cardHolderName, String expirationDate, String cvvCode, Client client, List<Ticket> tickets) {
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationDate = expirationDate;
        this.cvvCode = cvvCode;
        this.client = client;
        this.tickets = tickets;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }
}
