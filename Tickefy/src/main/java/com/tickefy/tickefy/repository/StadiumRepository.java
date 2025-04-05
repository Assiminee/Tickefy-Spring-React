package com.tickefy.tickefy.repository;


import com.tickefy.tickefy.entities.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, UUID> {


    Optional<Stadium> findByNameAndCity(String name, String city);



}
