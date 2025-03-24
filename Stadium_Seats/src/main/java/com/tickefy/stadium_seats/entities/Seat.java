package com.tickefy.stadium_seats.entities;


import com.tickefy.stadium_seats.entities.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int seatNumber;

    private String gate;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private double price;

    private boolean occupied;

    @ManyToOne
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;
}
