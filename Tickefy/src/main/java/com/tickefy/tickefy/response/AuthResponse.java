package com.tickefy.tickefy.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class AuthResponse {

	private String jwt;

	public AuthResponse(String jwt) {
		this.jwt = jwt;
	}

	public AuthResponse() {}

	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

}
