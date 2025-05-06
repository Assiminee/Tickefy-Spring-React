package com.tickefy.tickefy.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
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



    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client; // Reference to the client who made the purchase

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ticket> tickets; // Tickets associated with this purchase




    public Purchase() {
    }

    public Purchase(UUID id, int amount, double totalPrice, Client client, List<Ticket> tickets) {
        this.id = id;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.client = client;
        this.tickets = tickets;
    }

    public Purchase(int amount, double totalPrice, Client client, List<Ticket> tickets) {
        this.amount = amount;
        this.totalPrice = totalPrice;
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


}
