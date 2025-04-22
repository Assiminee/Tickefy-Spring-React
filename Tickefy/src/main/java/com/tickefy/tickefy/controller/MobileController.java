package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/mobile/users")
public class MobileController {


    private final UserService userService;

    @Autowired
    MobileController(UserService userService) {
        this.userService = userService;
    }

     @GetMapping("/{userId}") // to fetch the client's info after the facial recognition
    public ResponseEntity<?> getClientFromId (@PathVariable UUID userId){

        Client client = userService.getUserById(userId);
        String fullName = client.getF_name() +" "+ client.getL_name();

        return new ResponseEntity<>(fullName , HttpStatus.OK);
    }
}
}
