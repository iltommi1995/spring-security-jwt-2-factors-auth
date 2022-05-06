package com.z9devs.SpringSecurityJWT2FactorsAuth.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
	User findByUsername(String username);

	// Metodo per abilitare l'user quando conferma la mail
	@Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.enabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);
}