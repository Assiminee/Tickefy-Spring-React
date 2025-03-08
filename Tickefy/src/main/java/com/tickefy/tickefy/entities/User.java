package com.tickefy.tickefy.entities;


import com.tickefy.tickefy.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String f_name;

    private String l_name;

    private String email;

    private String password;

    @Temporal(TemporalType.DATE)
    private Date birthdate;

    private String phone;

    private Role role;

    private String profile_picture;

    @CreationTimestamp
    private LocalDateTime createdAt ;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(UUID id, String f_name, String l_name, String email, String password, Date birthdate, String phone, Role role, String profile_picture) {
        this.id = id;
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.phone = phone;
        this.role = role;
        this.profile_picture = profile_picture;
    }

    public User(String f_name, String l_name, String email, String password, Date birthdate, String phone, Role role, String profile_picture) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.phone = phone;
        this.role = role;
        this.profile_picture = profile_picture;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
