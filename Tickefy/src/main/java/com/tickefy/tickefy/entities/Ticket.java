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

    private String matchName;

    private String matchDate;

    private String qrCode; // Unique QR code for entry verification

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;  // Linking ticket to a seat

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase; // The purchase this ticket belongs to


    public Ticket() {
    }

    public Ticket(UUID id, LocalDateTime purchaseDate, String matchName, String matchDate, String qrCode, Seat seat, Purchase purchase) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.qrCode = qrCode;
        this.seat = seat;
        this.purchase = purchase;
    }

    public Ticket(LocalDateTime purchaseDate, String matchName, String matchDate, String qrCode, Seat seat, Purchase purchase) {
        this.purchaseDate = purchaseDate;
        this.matchName = matchName;
        this.matchDate = matchDate;
        this.qrCode = qrCode;
        this.seat = seat;
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

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }
}

