package com.tickefy.stadium_seats.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString


@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String city;

    private String address;

    private String country;

    private int gateCount;

    @OneToMany(mappedBy = "stadium", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;
}
