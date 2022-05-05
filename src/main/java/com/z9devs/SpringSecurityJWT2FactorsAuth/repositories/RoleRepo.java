package com.z9devs.SpringSecurityJWT2FactorsAuth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
