package com.tickefy.tickefy.entities;


import com.tickefy.tickefy.entities.enums.Role;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
public class Client extends User {

    private String nationality;

    private boolean flagged;

    private boolean hasImage;


    public Client(String nationality, boolean flagged) {
        this.nationality = nationality;
        this.flagged = flagged;
    }

    public Client(UUID id, String f_name, String l_name, String email, String password, String birthdate, String phone, Role role, String profile_picture, String nationality, boolean flagged,boolean hasImage) {
        super(id, f_name, l_name, email, password, birthdate, phone, role, profile_picture);
        this.nationality = nationality;
        this.flagged = flagged;
        this.hasImage = hasImage;
    }

    public Client(String f_name, String l_name, String email, String password, String birthdate, String phone, Role role, String profile_picture, String nationality, boolean flagged,boolean hasImage) {
        super(f_name, l_name, email, password, birthdate, phone, role, profile_picture);
        this.nationality = nationality;
        this.flagged = flagged;
        this.hasImage = hasImage;
    }

    public Client() {

    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
