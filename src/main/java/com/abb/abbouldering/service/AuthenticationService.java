package com.abb.abbouldering.service;

import java.util.regex.Pattern;

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
import com.abb.abbouldering.exception.InvalidCredentialsException;
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

	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
	private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
	private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

	public AuthenticationResponse register(RegisterRequest request) throws InvalidCredentialsException {
		
		if (userRepo.findByEmailIgnoreCase(request.getEmail()).isPresent())
			throw new InvalidCredentialsException("Email is already registered");
		
		if (!passwordPattern.matcher(request.getPassword()).matches()) 
			throw new InvalidCredentialsException("Invalid password");
		
		if (!emailPattern.matcher(request.getEmail()).matches()) 
			throw new InvalidCredentialsException("Invalid email");

		User user = new UserBuilder().firstName(request.getFirstName()).lastName(request.getLastName())
				.email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(Role.USER)
				.build();
		userRepo.save(user);
		String generatedJwt = jwtService.generateToken(user);
		return new AuthenticationResponse(generatedJwt);
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		User user = userRepo.findByEmailIgnoreCase(request.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		String generatedJwt = jwtService.generateToken(user);
		return new AuthenticationResponse(generatedJwt);
	}

}
