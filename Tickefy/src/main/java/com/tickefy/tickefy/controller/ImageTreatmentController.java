package com.tickefy.tickefy.controller;


import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.exceptions.BadRequestException;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.response.AuthResponse;
import com.tickefy.tickefy.response.JsonResponse;
import com.tickefy.tickefy.service.ImageQualityService;
import com.tickefy.tickefy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

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

        // Check if face image is provided
        if (facePhoto == null || facePhoto.isEmpty()) {
            System.out.println("Face image is required");
            throw new BadRequestException("Face image is required");
        }

        try {
            // Get the logged-in user
            Client loggedUser = (Client) userService.getProfile(jwt);


            // Assess image quality using FastAPI microservice
            Map<String, Object> result = imageQualityService.assessImageQuality(loggedUser.getId(), facePhoto);

            int status = (int) result.getOrDefault("status", 500);
            String clientIdStr = (String) result.get("label");

            if (status == 409) {

                UUID clientId = UUID.fromString(clientIdStr);
                String newToken = userService.conflictClient(clientId, loggedUser);

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new AuthResponse(newToken));
            }

            boolean isImageValid = (boolean) result.getOrDefault("is_image_valid", false);
            String message = (String) result.getOrDefault("message", "Unknown error");


            System.out.println(message);

            if (!isImageValid) {
                return ResponseEntity
                        .badRequest()
                        .body(new JsonResponse(message));
            }

            loggedUser.setHasImage(true);  // user gave us his image
            clientRepository.save(loggedUser);

            return ResponseEntity.ok(new JsonResponse("Image quality validated."));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponse("Image Treatment Error: " + e.getMessage()));
        }
    }
}
