package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.config.JwtProvider;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.enums.Role;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.response.AuthResponse;
import com.tickefy.tickefy.service.CustomerUserServiceImplementation;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private CustomerUserServiceImplementation customUserDetails;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          CustomerUserServiceImplementation customUserDetails, UserService userService) {
        super();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetails = customUserDetails;
        this.userService = userService;
    }



    @PostMapping("/signup")
    public ResponseEntity<?> createUserHandler(
            @RequestParam("firstName") String firstName
            , @RequestParam("lastName") String lastName
            , @RequestParam("email") String email
            , @RequestParam("password") String password
            , @RequestParam("phone") String phone
            , @RequestParam("birthdate") Date birthDate
            , @RequestParam("nationality") String nationality
            , @RequestParam("profilePicture") MultipartFile profilePicture
    ) throws Exception
    {
        try {
            if(userRepository.existsByEmail(email))
                throw new ConflictException("Email already exists");

            //create new Client

            Client newUser = new Client();

//            try {
//                newUser.setGender(Gender.valueOf(gender));
//            } catch (Exception e) {
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            }

            newUser.setBirthdate(birthDate);
            newUser.setEmail(email);
            newUser.setF_name(firstName);
            newUser.setL_name(lastName);
            newUser.setNationality(nationality);
            newUser.setPhone(phone);
            newUser.setRole(Role.ROLE_CLIENT);
            newUser.setFlagged(false);
            newUser.setPassword(passwordEncoder.encode(password));


            userService.insertUser(newUser, profilePicture);

            Authentication authentication = new UsernamePasswordAuthenticationToken(email , password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = JwtProvider.generateToken(authentication);


            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(token);

            return new ResponseEntity<>(authResponse , HttpStatus.CREATED);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
