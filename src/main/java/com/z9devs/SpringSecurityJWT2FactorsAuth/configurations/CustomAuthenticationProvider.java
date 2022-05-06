package com.z9devs.SpringSecurityJWT2FactorsAuth.configurations;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.Role;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;
import com.z9devs.SpringSecurityJWT2FactorsAuth.repositories.RoleRepo;
import com.z9devs.SpringSecurityJWT2FactorsAuth.repositories.UserRepo;

import lombok.AllArgsConstructor;

// https://www.baeldung.com/spring-security-authentication-provider
@AllArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	private UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
		String username = auth.getPrincipal() + "";
	    String password = auth.getCredentials() + "";
	    User user = userRepo.findByUsername(username);
	    if (user == null) {
	        throw new BadCredentialsException(String.format("User with username %s doas not exist", username));
	    }
	   
	    if (!user.isEnabled()) {
	        throw new DisabledException(String.format("User with username %s exists but is not enabled", username));
	    }
	    
	    if(!passwordEncoder.matches(password, user.getPassword())) {
	    	throw new BadCredentialsException("Wrong password");
	    }
	    org.springframework.security.core.userdetails.User u = new org.springframework.security.core.userdetails.User(username, password, user.getAuthorities());
	    
	    return new UsernamePasswordAuthenticationToken(u, null, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);

	}

}
