package com.tickefy.tickefy.service;

import com.tickefy.tickefy.entities.Client;
import com.tickefy.tickefy.entities.User;
import com.tickefy.tickefy.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {

    public User getProfile(String jwt);

    public List<User> getAllUsers();

    public void insertUser(User newUser, MultipartFile profilePicture) throws Exception;

    public User updateUser(Client existingUser, Client updatedUser, MultipartFile profilePicture) throws Exception;

    public void deleteUser(String jwt) throws ResourceNotFoundException;

    public User getUserById(UUID userId);
}
