package com.tickefy.tickefy.entities;


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
    private List<Ticket> tickets; // Tickets associated with this purchase

    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment; // Associated payment for this purchase
}
