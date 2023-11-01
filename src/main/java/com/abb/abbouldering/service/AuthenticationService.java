package com.abb.abbouldering.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.config.JwtService;
import com.abb.abbouldering.dto.AuthenticationRequest;
import com.abb.abbouldering.dto.AuthenticationResponse;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.repository.UserRepository;

@Service
public class AuthenticationService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired 
	private AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		UserBuilder userBuilder = new UserBuilder();
		User user = userBuilder
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();
		userRepo.save(user);
		System.out.println("New User" + user);
		String generatedJwt = jwtService.generateToken(user);
		return new AuthenticationResponse(generatedJwt);
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		User user = userRepo.findByEmailIgnoreCase(request.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		String generatedJwt = jwtService.generateToken(user);
		return new AuthenticationResponse(generatedJwt);
	}

}
