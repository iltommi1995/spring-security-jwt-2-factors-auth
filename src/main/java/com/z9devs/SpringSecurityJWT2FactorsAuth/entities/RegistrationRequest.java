package com.z9devs.SpringSecurityJWT2FactorsAuth.entities;

import lombok.Data;

@Data
public class RegistrationRequest {
	private  String firstName;
	private  String lastName;
	private String username;
	private  String email;
	private  String password;
}
