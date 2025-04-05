package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Stadium;
import org.springframework.stereotype.Service;

@Service
public interface StadiumService {


    public Stadium getOrCreateStadium(String name, String city);
}
