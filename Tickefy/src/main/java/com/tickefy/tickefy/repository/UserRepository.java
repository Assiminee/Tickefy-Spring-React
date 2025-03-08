package com.tickefy.tickefy.repository;


import com.tickefy.tickefy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdIsNotAndEmail(UUID id, String email);
}
