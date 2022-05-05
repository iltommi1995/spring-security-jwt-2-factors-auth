package com.z9devs.SpringSecurityJWTExample;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.z9devs.SpringSecurityJWTExample.entities.Role;
import com.z9devs.SpringSecurityJWTExample.entities.User;
import com.z9devs.SpringSecurityJWTExample.services.UserService;
// https://www.youtube.com/watch?v=VVn9OG9nfH0
@SpringBootApplication
public class SpringSecurityJwtExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtExampleApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
			
			userService.saveUser(new User(null, "John Travolta", "john", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Mario Rossi", "mario", "1234", new ArrayList<>()));
			
			userService.addRoleToUser("john", "ROLE_USER");
			userService.addRoleToUser("will", "ROLE_ADMIN");
			userService.addRoleToUser("jim", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("mario", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("mario", "ROLE_USER");
		};
	}
}
