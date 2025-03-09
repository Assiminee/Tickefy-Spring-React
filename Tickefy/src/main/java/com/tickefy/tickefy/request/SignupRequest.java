package com.tickefy.tickefy.request;


import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

public class SignupRequest {

    private String f_name;

    private String l_name;

    private String email;

    private String password;

    private Date birthdate;

    private String phone;


    public SignupRequest() {
    }

    public SignupRequest(String f_name, String l_name, String email, String password, Date birthdate, String phone) {
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
