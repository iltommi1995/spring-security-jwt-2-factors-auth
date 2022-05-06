package com.z9devs.SpringSecurityJWT2FactorsAuth.utils;

import java.util.function.Predicate;

import org.springframework.stereotype.Service;

// TODO -> A cosa serve?
@Service
public class EmailValidator implements Predicate<String> {

	@Override
	public boolean test(String t) {
		if(!t.contains("@"))
			return false;
	
		if(!t.contains(".it") && !t.contains(".com") && !t.contains(".org"))
			return false;
		
		return true;
	}

}
