package com.abb.abbouldering.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.abb.abbouldering.model.Role;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
		.authorizeHttpRequests(
				authorize -> authorize
					.requestMatchers("/api/v1/auth/**", "/api/v1/events/all", "/api/v1/events/all/**", "/api/v1/mail", "/api/v1/stripe")
					.permitAll()
					.requestMatchers("/api/v1/events/user/**")
					.hasAnyAuthority("USER", "ADMIN")
					.requestMatchers("/api/v1/events/**", "/api/v1/events", "/api/v1/users/get-all-admin", "/api/v1/users/new-admin")
					.hasAuthority("ADMIN")
					.anyRequest()
					.authenticated()
		)
		.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
