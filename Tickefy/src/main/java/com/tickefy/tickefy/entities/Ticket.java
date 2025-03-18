package com.tickefy.tickefy.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime purchaseDate;
    private String matchEndpoint; // Endpoint linking to the match details
    private String seatEndpoint;  // Endpoint linking to seat details
    private String qrCode; // Unique QR code for entry verification

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase; // The purchase this ticket belongs to
}

