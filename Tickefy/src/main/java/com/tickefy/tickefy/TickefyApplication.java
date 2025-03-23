package com.tickefy.tickefy;

import com.tickefy.tickefy.entities.Ticket;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TickefyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TickefyApplication.class, args);
        System.out.println("Tickefy-MAIN");

    }

    @Override
    public void run(String... args) throws Exception {

//        Ticket ticket = new Ticket();
//        ticket.setMatchEndpoint("testMatch");
//        ticket.setSeatEndpoint("testSeat");
//
//        System.out.println(ticket.getMatchEndpoint());
//        System.out.println(ticket.getSeatEndpoint());
    }
}
