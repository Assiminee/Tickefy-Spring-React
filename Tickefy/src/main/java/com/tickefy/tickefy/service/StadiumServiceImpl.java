package com.tickefy.tickefy.service;


import com.tickefy.tickefy.entities.Stadium;
import com.tickefy.tickefy.repository.StadiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StadiumServiceImpl implements StadiumService {


    private final StadiumRepository stadiumRepository;

    @Autowired
    public StadiumServiceImpl(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }


    public Stadium getOrCreateStadium(String name, String city) {

        return stadiumRepository.findByNameAndCity(name,city)
                .orElseGet(() -> {
                    // Stadium does not exist, create a new one
                    Stadium newStadium = new Stadium();
                    newStadium.setName(name);
                    newStadium.setCity(city);

                    return stadiumRepository.save(newStadium);
                });
    }

}
