package com.tickefy.tickefy.service;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    public Authentication authenticate(String username, String password);
}
