package com.tickefy.tickefy;

import com.tickefy.tickefy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TickefyApplication implements CommandLineRunner {
    @Autowired
    private UserRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

        repo.save(androidClient);
    }
}
