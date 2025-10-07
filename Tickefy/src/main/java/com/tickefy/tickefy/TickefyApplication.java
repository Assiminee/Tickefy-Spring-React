package com.tickefy.tickefy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.enums.Role;

@SpringBootApplication
public class TickefyApplication implements CommandLineRunner {
    private UserRepository repo;

    public static void main(String[] args) {
        SpringApplication.run(TickefyApplication.class, args);
        System.out.println("Tickefy-MAIN");

    }

    @Override
    public void run(String... args) throws Exception {
        Client androidClient = new Client();

        androidClient.setEmail("android-client@tickefy.com");
        androidClient.setF_name("Android");
        androidClient.setL_name("Client");
        androidClient.setRole(Role.ROLE_ADMIN);
        androidClient.setPassword(passwordEncoder.encode("AndroidClient@1"));
    }
}
