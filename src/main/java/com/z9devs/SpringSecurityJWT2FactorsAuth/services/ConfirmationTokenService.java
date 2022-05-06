package com.z9devs.SpringSecurityJWT2FactorsAuth.services;

import java.util.Optional;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.ConfirmationToken;

public interface ConfirmationTokenService {

	
	void saveConfirmationToken(ConfirmationToken token);

	Optional<ConfirmationToken> getToken(String token);

	int setConfirmedAt(String token);

}
