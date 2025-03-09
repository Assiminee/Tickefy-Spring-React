package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.dto.UserDTO;
import com.tickefy.tickefy.exceptions.ConflictException;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.exceptions.UnauthorizedException;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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

            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setF_name(user.getF_name());
            userDTO.setL_name(user.getL_name());
            userDTO.setEmail(user.getEmail());
            userDTO.setProfile_picture(user.getProfile_picture());
            userDTO.setBirthdate(user.getBirthdate());
            userDTO.setRole(String.valueOf(user.getRole()));
            userDTO.setPhone(user.getPhone());
           // userDTO.setNationality();

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

		return new ResponseEntity<>(users , HttpStatus.OK);
	}



    @PutMapping
    public ResponseEntity<?> updateLoggedInUser (@RequestHeader("Authorization") String jwt
            , @RequestParam("f_name") String f_name
            , @RequestParam("l_name") String l_name
            , @RequestParam("email") String email
            , @RequestParam("password") String password
            , @RequestParam("phone") String phone
            , @RequestParam("birthdate") Date birthdate
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
}
