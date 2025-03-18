package com.tickefy.tickefy.entities;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Payment {

    @Id
    private UUID transactionId;

    @OneToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;
}
