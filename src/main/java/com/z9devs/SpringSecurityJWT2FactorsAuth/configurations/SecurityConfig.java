package com.z9devs.SpringSecurityJWT2FactorsAuth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;

import com.z9devs.SpringSecurityJWT2FactorsAuth.filters.CustomAuthenticationFilter;
import com.z9devs.SpringSecurityJWT2FactorsAuth.filters.CustomAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomAuthenticationProvider authProvider;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Qua diciamo come trovare gli user e diciamo di 
		// farlo con JPA, attraverso un service
		auth.userDetailsService(userDetailsService)
		// Diciamo poi di cryptare la pw con BCrypt
		.passwordEncoder(bCryptPasswordEncoder)
		//.and()
		// Setto authentication provider
		//.authenticationProvider(authProvider)
		;
		
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authProvider);
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		
		// Disabilito cross site request forgery
		http.csrf().disable()
		// Disabilito le sessioni
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		
		// Autorizzo chiunque ad accedere all'applicazione fino a questo punto
		.and() 
		.authorizeRequests().antMatchers("/api/login", "/api/token/refresh/**", "/api/registration/**").permitAll()
		.and()
		.authorizeRequests().antMatchers(HttpMethod.GET, "/api/user/**").hasAuthority("ROLE_USER")
		.and()
		.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN")
		.and()
		.authorizeRequests().antMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority("ROLE_ADMIN")
		.and()
		.authorizeRequests().anyRequest().authenticated()
		// .authorizeRequests().anyRequest().permitAll()
		
		
		// Filtri
		.and()
		// Filtro autenticazione
		//.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()))
		.addFilter(customAuthenticationFilter)
		// Filtro autorizzazione
		.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

	}
	
	
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
}
