package com.z9devs.SpringSecurityJWT2FactorsAuth.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticataionManager;
	
	public CustomAuthenticationFilter(AuthenticationManager authenticataionManager) {
		this.authenticataionManager = authenticataionManager;
	}
	
	// Lo user prova ad autenticarsi, passa da questo metodo
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		// Recupero dai parametri utente e pw, quindi devo
		// settare le credenziali nel body della request
		// come x-www-form-urlencoded
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info("Username is: {}", username);
		log.info("Password is: {}", password);
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		// Faccio fare autenticazione all'authentication manager
		return authenticataionManager.authenticate(authenticationToken);
	}

	// Se l'autenticazione ha successo viene chiamato questo metodo
	// e bisogna dare qui il JWT allo user
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		// Questo è lo user che si è autenticato correttamente
		User user = (User) authentication.getPrincipal();
		// Questo è l'algoritmo usato per la digital signature
		// Bisogna assicurarsi che il "secret" sia cryptato
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		
		String access_token = JWT.create()
				// Identificativo user
				.withSubject(user.getUsername())
				// Data scadenza del token
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 *60 * 1000 ))
				// Creatore del token
				.withIssuer(request.getRequestURL().toString())
				// Ruoli per l'user
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				// Firmo il token
				.sign(algorithm);
		
		String refresh_token = JWT.create()
				// Identificativo user
				.withSubject(user.getUsername())
				// Data scadenza del token
				.withExpiresAt(new Date(System.currentTimeMillis() + 30 *60 * 1000 ))
				// Creatore del token
				.withIssuer(request.getRequestURL().toString())
				// Firmo il token
				.sign(algorithm);
		
		// Inserisco i due token nell'header della response
		// della richiesta di autenticazione dell'user
		//response.setHeader("access_token", access_token);
		//response.setHeader("refresh_token", refresh_token);
		
		Map<String,String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.unsuccessfulAuthentication(request, response, failed);
	}
}
