package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.entities.dto.UserDTO;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
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

            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setF_name(user.getF_name());
            userDTO.setL_name(user.getL_name());
            userDTO.setEmail(user.getEmail());
            userDTO.setProfile_picture(user.getProfile_picture());
            userDTO.setBirthdate(user.getBirthdate());
            userDTO.setRole(String.valueOf(user.getRole()));
            userDTO.setPhone(user.getPhone());

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
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
}
