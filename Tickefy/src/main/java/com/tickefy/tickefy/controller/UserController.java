package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.dto.FullNameDTO;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.exceptions.UnauthorizedException;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.request.LoginRequest;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.FacialRecognitionService;
import com.tickefy.tickefy.service.TicketService;
import com.tickefy.tickefy.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    private final TicketService ticketService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final FacialRecognitionService facialRecognitionService;


    @Autowired
    public UserController(UserService userService,TicketService ticketService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepository,
                          FacialRecognitionService facialRecognitionService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.facialRecognitionService = facialRecognitionService;
    }


    @GetMapping("/test")
    public ResponseEntity<JsonResponse> test (@RequestHeader("Authorization") String jwt){

        User user = userService.getProfile(jwt);

        return new ResponseEntity<>(new JsonResponse("welcome to hell mfs"), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile (@RequestHeader("Authorization") String jwt){

        try {
            User user = userService.getProfile(jwt);
            user.setPassword("");

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
	public ResponseEntity<List<User>> getUsers (@RequestHeader("Authorization") String jwt){

		List<User> users = userService.getAllUsers();
        for(User user : users){
            user.setPassword("");
        }

		return new ResponseEntity<>(users , HttpStatus.OK);
	}



    @PutMapping
    public ResponseEntity<?> updateLoggedInUser (@RequestHeader("Authorization") String jwt
            , @RequestParam("f_name") @NotBlank(message = "First name is required.") String f_name
            , @RequestParam("l_name") @NotBlank(message = "Last name is required.") String l_name
            , @RequestParam("email") @NotBlank(message = "Email is required.") String email
            , @RequestParam("password") @NotBlank(message = "Password is required.") String password
            , @RequestParam("phone") @NotBlank(message = "Phone is required.") String phone
            , @RequestParam("birthdate") @NotBlank(message = "birthDate is required.") String birthdateString
            , @RequestParam("nationality") String nationality
            , @RequestParam("profilePicture") MultipartFile profilePicture) throws Exception
    {

        Client loggedUser = (Client) userService.getProfile(jwt);

        boolean isEmailExist = userRepository.existsByIdIsNotAndEmail(loggedUser.getId(),email);

        if (isEmailExist) {
            throw new ConflictException("User with this email already exists");
        }

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

            Client updatedUser = new Client();

            updatedUser.setBirthdate(birthDate);
            updatedUser.setEmail(email);
            updatedUser.setF_name(f_name);
            updatedUser.setL_name(l_name);
            updatedUser.setPassword(passwordEncoder.encode(password));
            updatedUser.setPhone(phone);
            updatedUser.setNationality(nationality);

            User user = userService.updateUser(loggedUser, updatedUser, profilePicture);
            user.setPassword("");

            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (Exception e) {
            e.getStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteLoggedInUser (
            @RequestHeader("Authorization") String jwt
    ) throws ResourceNotFoundException {

           userService.deleteUser(jwt);

           return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PostMapping("/identify")
    public ResponseEntity<?> identifyClient (@RequestHeader("Authorization") String jwt,
                                             @RequestParam(name = "facePhoto") MultipartFile facePhoto){

        // Check if face image is provided
        if (facePhoto == null || facePhoto.isEmpty()) {
            System.out.println("Face image is required");
            throw new BadRequestException("Face image is required");
        }

        try {

            Map<String, Object> response = facialRecognitionService.identifyClient(facePhoto);

            boolean identified = (Boolean) response.get("identified");
            String clientIdStr = (String) response.get("message");

            if (!identified) {
                System.out.println("Face could not be identified. Please try again.");
                return new ResponseEntity<>(new JsonResponse("Face could not be identified. Please try again."),
                        HttpStatus.BAD_REQUEST);
            }

            UUID clientId = UUID.fromString(clientIdStr);

            Optional<Ticket> ticketOpt = ticketService.findTodayWithTimeTicketByClient(clientId);
            System.out.println("Client identified with ID: " + clientId);

            if (ticketOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                String firstName = ticket.getPurchase().getClient().getF_name();
                String lastName = ticket.getPurchase().getClient().getL_name();

                FullNameDTO fullNameDTO = new FullNameDTO(firstName+ " " +lastName);

                System.out.println(fullNameDTO);
                return new ResponseEntity<>(fullNameDTO, HttpStatus.OK);
            } else {
                System.out.println("No ticket found for today for this client.");
                return new ResponseEntity<>(new JsonResponse("No ticket found for today for this client.")
                        ,HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new JsonResponse("An error occurred during client identification: "+
                    " " +e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping
    public ResponseEntity<?> resetClientCredentials (@RequestHeader("Authorization") String jwt,
                                                     @RequestBody LoginRequest loginRequest) {

        Client loggedUser = (Client) userService.getProfile(jwt);

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        boolean isEmailExist = userRepository.existsByIdIsNotAndEmail(loggedUser.getId(),email);
        if (isEmailExist) {
            throw new ConflictException("User with this email already exists");
        }

        try{

            if(email != null ) {
                loggedUser.setEmail(email);
            }
            if(password != null ) {
                loggedUser.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(loggedUser);

            return new ResponseEntity<>(new JsonResponse("Client Credentials Reset Successfully"),
                    HttpStatus.OK);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new JsonResponse("Error resetting Client's info : " +e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
