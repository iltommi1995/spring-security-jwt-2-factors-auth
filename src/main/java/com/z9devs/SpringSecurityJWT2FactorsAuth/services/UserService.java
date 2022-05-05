package com.z9devs.SpringSecurityJWT2FactorsAuth.services;

import java.util.List;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.Role;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;

public interface UserService {
	User saveUser(User user);
	Role saveRole(Role role);
	void addRoleToUser(String username, String roleName);
	User getUser(String username);
	List<User> getUsers();
}
