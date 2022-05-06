package com.z9devs.SpringSecurityJWT2FactorsAuth.services;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.RegistrationRequest;

public interface RegistrationService {

	String register(RegistrationRequest request);

	String confirmToken(String token);

}
