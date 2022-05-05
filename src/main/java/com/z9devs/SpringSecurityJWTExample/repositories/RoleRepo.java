package com.z9devs.SpringSecurityJWTExample.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.z9devs.SpringSecurityJWTExample.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
