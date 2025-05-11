package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.config.JwtProvider;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.enums.Role;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.request.LoginRequest;
import com.tickefy.tickefy.response.AuthResponse;
import com.tickefy.tickefy.service.AuthService;
import com.tickefy.tickefy.service.CustomerUserServiceImplementation;
import com.tickefy.tickefy.service.UserService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private UserService userService;

    private CustomerUserServiceImplementation customUserDetails;

    private AuthService authService;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          CustomerUserServiceImplementation customUserDetails, UserService userService,
                          AuthService authService) {
        super();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetails = customUserDetails;
        this.userService = userService;
        this.authService = authService;
    }



    @PostMapping("/signup")
    public ResponseEntity<?> createUserHandler(
              @RequestParam("f_name") @NotBlank(message = "First name is required.") String f_name
            , @RequestParam("l_name") @NotBlank(message = "Last name is required.") String l_name
            , @RequestParam("email") @NotBlank(message = "Email is required.") String email
            , @RequestParam("password") @NotBlank(message = "Password is required.") String password
            , @RequestParam("phone") @NotBlank(message = "Phone is required.") String phone
            , @RequestParam("birthdate") @NotBlank(message = "birthDate is required.") String birthdateString

    )
    {

        if(userRepository.existsByEmail(email))
            throw new ConflictException("Email already exists");

        // Example: matchDateString = "25/04/2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(birthdateString, formatter);
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Invalid birth date format. Please use yyyy-MM-dd.");
        }

        try {

            //create new Client
            Client newUser = new Client();

            newUser.setEmail(email);
            newUser.setF_name(f_name);
            newUser.setL_name(l_name);
            newUser.setPhone(phone);
            newUser.setBirthdate(birthDate);
            newUser.setRole(Role.ROLE_CLIENT);
            newUser.setFlagged(false);
            newUser.setPassword(passwordEncoder.encode(password));

            newUser.setHasImage(false); // user doesnt have an image yet

            userRepository.save(newUser);

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

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest loginRequest) {

        AuthResponse authResponse = new AuthResponse();

        try {
            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            System.out.println("Login Successful");
            System.out.println(username+ " ------- " +password);


            Authentication authentication = authService.authenticate(username , password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = JwtProvider.generateToken(authentication);

            authResponse.setJwt(token);

            return new ResponseEntity<>(authResponse , HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    //authenticate methode to check user and motdepasse
//    private Authentication authenticate(String username, String password) {
//
//        UserDetails userDetails = customUserDetails.loadUserByUsername(username);
//
//        System.out.println("Sign in userDetails - " +userDetails);
//
//        if(userDetails == null) {
//            System.out.println("Sign in UserDetails - null " + userDetails);
//            throw new BadCredentialsException("Invalid username or password");
//        }
//
//        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
//            System.out.println("sign in userDetails - password not match " +userDetails);
//            throw new BadCredentialsException("Invalid username or password");
//        }
//
//        return new UsernamePasswordAuthenticationToken(userDetails, null , userDetails.getAuthorities());
//
//
//    }






}
