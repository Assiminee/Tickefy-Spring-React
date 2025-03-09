package com.tickefy.tickefy.entities.dto;



import com.tickefy.tickefy.entities.enums.Role;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


public class UserDTO {

	private UUID id;

	private String f_name;

	private String l_name;

	private String email;

	private Date birthdate;

	private String phone;

	private String role;

	private String profile_picture;

	@CreationTimestamp
	private LocalDateTime createdAt ;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private String nationality;

	private String flagged;

	public UserDTO() {

	}

	public UserDTO(UUID id, String f_name, String l_name, String email, String phone, Date birthdate, String role, LocalDateTime createdAt, String profile_picture, LocalDateTime updatedAt, String nationality, String flagged) {
		this.id = id;
		this.f_name = f_name;
		this.l_name = l_name;
		this.email = email;
		this.phone = phone;
		this.birthdate = birthdate;
		this.role = role;
		this.createdAt = createdAt;
		this.profile_picture = profile_picture;
		this.updatedAt = updatedAt;
		this.nationality = nationality;
		this.flagged = flagged;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
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

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getFlagged() {
		return flagged;
	}

	public void setFlagged(String flagged) {
		this.flagged = flagged;
	}
}
