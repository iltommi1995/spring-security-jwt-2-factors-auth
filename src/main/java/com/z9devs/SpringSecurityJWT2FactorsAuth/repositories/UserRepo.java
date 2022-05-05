package com.z9devs.SpringSecurityJWT2FactorsAuth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
