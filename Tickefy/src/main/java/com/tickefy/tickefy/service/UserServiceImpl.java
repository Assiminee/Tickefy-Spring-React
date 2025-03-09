package com.tickefy.tickefy.service;


import com.tickefy.tickefy.config.JwtProvider;
import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import com.tickefy.tickefy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images";


    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getProfile(String jwt) throws ResourceNotFoundException {

        String email = JwtProvider.getEmailFromJwtToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
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
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
