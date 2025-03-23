package com.tickefy.tickefy.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.UUID;




@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    private LocalDateTime purchaseDate;

    private String matchName; // Endpoint linking to the match details

    private int seatNumber;  // Endpoint linking to seat details

    private String qrCode; // Unique QR code for entry verification

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase; // The purchase this ticket belongs to


    public Ticket() {
    }

    public Ticket(UUID id, LocalDateTime purchaseDate, String matchName, int seatNumber, String qrCode, Purchase purchase) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.matchName = matchName;
        this.seatNumber = seatNumber;
        this.qrCode = qrCode;
        this.purchase = purchase;
    }

    public Ticket(LocalDateTime purchaseDate, String matchName, int seatNumber, String qrCode, Purchase purchase) {
        this.purchaseDate = purchaseDate;
        this.matchName = matchName;
        this.seatNumber = seatNumber;
        this.qrCode = qrCode;
        this.purchase = purchase;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }


    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}

