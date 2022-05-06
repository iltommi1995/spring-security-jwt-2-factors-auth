package com.z9devs.SpringSecurityJWT2FactorsAuth.servicesImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.ConfirmationToken;
import com.z9devs.SpringSecurityJWT2FactorsAuth.repositories.ConfirmationTokenRepo;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
	private final ConfirmationTokenRepo confirmationTokenRepo;
	
	@Override
	public void saveConfirmationToken(ConfirmationToken token) {
		confirmationTokenRepo.save(token);
	}

	@Override
	public Optional<ConfirmationToken> getToken(String token) {
		return confirmationTokenRepo.findByToken(token);
	}
	
	@Override
	public int setConfirmedAt(String token) {
        return confirmationTokenRepo.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
