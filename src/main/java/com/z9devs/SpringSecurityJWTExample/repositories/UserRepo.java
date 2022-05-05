package com.z9devs.SpringSecurityJWTExample.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.z9devs.SpringSecurityJWTExample.entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
