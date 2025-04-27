package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Ticket;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.dto.FullNameDTO;
import com.tickefy.tickefy.entities.dto.UserDTO;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.exceptions.UnauthorizedException;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.service.FacialRecognitionService;
import com.tickefy.tickefy.service.TicketService;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
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
    public ResponseEntity<String> test (@RequestHeader("Authorization") String jwt){

        User user = userService.getProfile(jwt);

        return new ResponseEntity<>("welcome to hell mfs", HttpStatus.OK);
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
            , @RequestParam("f_name") String f_name
            , @RequestParam("l_name") String l_name
            , @RequestParam("email") String email
            , @RequestParam("password") String password
            , @RequestParam("phone") String phone
            , @RequestParam("birthdate") String birthdate
            , @RequestParam("nationality") String nationality
            , @RequestParam("profilePicture") MultipartFile profilePicture) throws Exception
    {

        Client loggedUser = (Client) userService.getProfile(jwt);

        boolean isEmailExist = userRepository.existsByIdIsNotAndEmail(loggedUser.getId(),email);

        if (isEmailExist) {
            throw new ConflictException("User with this email already exists");
        }

        try {

            Client updatedUser = new Client();

            updatedUser.setBirthdate(birthdate);
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

        try {

            // Check if face image is provided
            if (facePhoto == null || facePhoto.isEmpty()) {
                return new ResponseEntity<>("Face image is required",
                        HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = facialRecognitionService.identifyClient(facePhoto);

            boolean identified = (Boolean) response.get("identified");
            String clientIdStr = (String) response.get("message");

            if (!identified) {
                return new ResponseEntity<>("Face could not be identified. Please try again.",
                        HttpStatus.BAD_REQUEST);
            }

            UUID clientId = UUID.fromString(clientIdStr);

            Optional<Ticket> ticketOpt = ticketService.findTodayTicketByClient(clientId);

            if (ticketOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                String firstName = ticket.getPurchase().getClient().getF_name();
                String lastName = ticket.getPurchase().getClient().getL_name();

                FullNameDTO fullNameDTO = new FullNameDTO(firstName+ " " + lastName);

                return new ResponseEntity<>(fullNameDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No ticket found for today for this client."
                        ,HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred during client identification: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
