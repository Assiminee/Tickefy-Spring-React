package com.tickefy.tickefy.service;


import com.tickefy.tickefy.config.JwtProvider;
import com.tickefy.tickefy.controller.AuthController;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.Purchase;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.ClientRepository;
import com.tickefy.tickefy.repository.PurchaseRepository;
import com.tickefy.tickefy.repository.UserRepository;
import com.tickefy.tickefy.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final ClientRepository clientRepository;

    private final PurchaseRepository purchaseRepository;

    private final CustomerUserServiceImplementation customUserDetails;

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images";


    @Autowired
    public UserServiceImpl(UserRepository userRepository, ClientRepository clientRepository,
                           PurchaseRepository purchaseRepository,CustomerUserServiceImplementation customUserDetails) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.purchaseRepository = purchaseRepository;
        this.customUserDetails = customUserDetails;
    }

    @Override
    public User getProfile(String jwt) throws ResourceNotFoundException {

        String email = JwtProvider.getEmailFromJwtToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new ResourceNotFoundException("User profile not found");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public void insertUser(User newUser, MultipartFile profilePicture) throws Exception {

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = profilePicture.getOriginalFilename();

        if (originalFileName != null && !originalFileName.isEmpty()) {
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex != -1) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = originalFileName.replace(fileExtension, "") + "_" + Instant.now().toEpochMilli()
                    + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);
            profilePicture.transferTo(filePath.toFile());

            newUser.setProfile_picture(uniqueFileName);

            userRepository.save(newUser);

        }

    }

    @Override
    public User updateUser(Client existingUser, Client updatedUser, MultipartFile profilePicture) throws Exception {


        if(updatedUser.getF_name() != null ) {
            existingUser.setF_name(updatedUser.getF_name());
        }
        if(updatedUser.getL_name() != null ) {
            existingUser.setL_name(updatedUser.getL_name());
        }
        if(updatedUser.getEmail() != null ) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if(updatedUser.getBirthdate() != null ) {
            existingUser.setBirthdate(updatedUser.getBirthdate());
        }
        if(updatedUser.getPassword() != null ) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        if(updatedUser.getPhone() != null ) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if(updatedUser.getNationality() != null ) {
            existingUser.setNationality(updatedUser.getNationality());
        }


        if(profilePicture !=null) {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = profilePicture.getOriginalFilename();

            if (originalFileName != null && !originalFileName.isEmpty()) {
                String fileExtension = "";
                int dotIndex = originalFileName.lastIndexOf(".");
                if (dotIndex != -1) {
                    fileExtension = originalFileName.substring(dotIndex);
                }
                String uniqueFileName = originalFileName.replace(fileExtension, "") + "_" + Instant.now().toEpochMilli()
                        + fileExtension;
                Path filePath = uploadPath.resolve(uniqueFileName);
                profilePicture.transferTo(filePath.toFile());

                existingUser.setProfile_picture(uniqueFileName);

            }
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String jwt) throws ResourceNotFoundException {

        String email = JwtProvider.getEmailFromJwtToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.delete(user);

    }

    @Override
    public Client getUserById(UUID userId) {

        Client client =  clientRepository.findById(userId).orElse(null);

        if (client == null) {
            throw new ResourceNotFoundException("Client not found");
        }
        return client;
    }

    @Override
    public String conflictClient(UUID clientId, Client loggedUser) throws ResourceNotFoundException {

       Client oldClient = clientRepository.findById(clientId)
               .orElseThrow(() -> new ResourceNotFoundException("Old Client's Account was not found"));

       List<Purchase> purchases = purchaseRepository.findByClient(loggedUser);

        // Move purchases if they exist
        if (!purchases.isEmpty()) {
            for (Purchase purchase : purchases) {
                purchase.setClient(oldClient);
            }
            purchaseRepository.saveAll(purchases);
            System.out.println("All Purchases were passed to the old account");
        }

        // Delete the new account
        clientRepository.deleteById(loggedUser.getId());

        // Bypass password check and log in old client
        UserDetails userDetails = customUserDetails.loadUserByUsername(oldClient.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return JwtProvider.generateToken(authentication);
    }
}
