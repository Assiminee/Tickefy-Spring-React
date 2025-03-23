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

    public Payment() {
    }

    public Payment(UUID transactionId, Purchase purchase) {
        this.transactionId = transactionId;
        this.purchase = purchase;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public Purchase getPurchase() {
        return purchase;
    }
    
    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}
