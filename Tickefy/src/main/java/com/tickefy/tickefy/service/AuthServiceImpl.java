package com.tickefy.tickefy.service;


import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


    private final PasswordEncoder passwordEncoder;

    private final CustomerUserServiceImplementation customUserDetails;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, CustomerUserServiceImplementation customUserDetails) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetails = customUserDetails;
    }

    //authenticate methode to check user and password
    @Override
    public Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("Sign in userDetails - " +userDetails);

        if(userDetails == null) {
            System.out.println("Sign in UserDetails - null " + userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("sign in userDetails - password not match " +userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null , userDetails.getAuthorities());

    }
}
