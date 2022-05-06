package com.z9devs.SpringSecurityJWT2FactorsAuth.servicesImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.ConfirmationToken;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.Role;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;
import com.z9devs.SpringSecurityJWT2FactorsAuth.repositories.RoleRepo;
import com.z9devs.SpringSecurityJWT2FactorsAuth.repositories.UserRepo;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.ConfirmationTokenService;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
// Lombok fa inject delle proprietÃ  direttamente
@RequiredArgsConstructor
@Transactional
// Annotazione per i log
@Slf4j
// UserDetailService è l'interfaccia usata per trovare effettivamente
// gli user
public class UserServiceImpl implements UserService, UserDetailsService {
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final PasswordEncoder passwordEncoder;
	private final ConfirmationTokenService confirmationTokenService;
	
	// TODO -> gestire anche controllo mail?
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if(user == null) {
			log.error("User not found in the database");
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.info("User found in the database: {}", username);
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> { 
			authorities.add(new SimpleGrantedAuthority(role.getName())); 
		});
		
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
	}
	
	// TODO -> gestire anche mail?
	@Override
	public String signUpUser(User user) {
		// Controllo se esiste lo user
		// o per user o per mail, forse devo integrare la mail
		boolean userExists = userRepo.findByUsername(user.getUsername()) == null ? false : true;
		if(userExists) {
			throw new IllegalStateException("Email already taken");
		}
		Role r = roleRepo.findByName("ROLE_USER");
		Collection<Role> roles = new ArrayList<Role>();
		roles.add(r);
		user.setRoles(roles);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		userRepo.save(user);
		
		// TODO: Send confirmation token
		String to = UUID.randomUUID().toString();
		ConfirmationToken token = new ConfirmationToken(
				to,
				LocalDateTime.now(),
				LocalDateTime.now().plusMinutes(15),
				user
				);
		confirmationTokenService.saveConfirmationToken(token);
		
		return to;
	}
	
	@Override
	public User saveUser(User user) {
		log.info("Saving new user {} to the database", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		log.info("Saving role {} to user {}", roleName, username);
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByName(roleName);
		user.getRoles().add(role);
	}

	@Override
	public User getUser(String username) {
		log.info("Fetching user {}", username);
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		log.info("Fetching all users");
		return userRepo.findAll();
	}

	@Override
	public void enableUser(String email) {
		userRepo.enableUser(email);
	}
}
