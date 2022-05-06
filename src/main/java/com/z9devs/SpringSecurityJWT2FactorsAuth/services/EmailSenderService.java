package com.z9devs.SpringSecurityJWT2FactorsAuth.services;

public interface EmailSenderService {
	void send(String to, String email);
}
