package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.ImageQualityService;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageTreatmentController {


    private final UserService userService;

    private final ClientRepository clientRepository;

    private final ImageQualityService imageQualityService;


    @Autowired
    public ImageTreatmentController(UserService userService, ImageQualityService imageQualityService, ClientRepository clientRepository) {
        this.userService = userService;
        this.imageQualityService = imageQualityService;
        this.clientRepository = clientRepository;
    }

    @PostMapping
    public ResponseEntity<?> assessImageQuality(@RequestHeader("Authorization") String jwt,
                                       @RequestParam(name = "facePhoto") MultipartFile facePhoto) {
        try {
            // Get the logged-in user
            Client loggedUser = (Client) userService.getProfile(jwt);

            // Check if face image is provided
            if (facePhoto == null || facePhoto.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new JsonResponse("Face image is required"));
            }

            // Assess image quality using FastAPI microservice
            Map<String, Object> result = imageQualityService.assessImageQuality(loggedUser.getId(), facePhoto);


            System.out.println(result);
            boolean isImageValid = (boolean) result.getOrDefault("is_image_valid", false);
            String message = (String) result.getOrDefault("message", "Unknown error");
            System.out.println(message);

            if (!isImageValid) {
                return ResponseEntity
                        .badRequest()
                        .body(message);
            }
            loggedUser.setHasImage(true);  // user gave us his image
            clientRepository.save(loggedUser);


            return ResponseEntity.ok("Image quality validated.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Image Treatment Error: " + e.getMessage());
        }
    }
}
