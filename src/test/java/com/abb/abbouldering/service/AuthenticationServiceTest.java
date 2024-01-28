package com.abb.abbouldering.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abb.abbouldering.config.JwtService;
import com.abb.abbouldering.dto.AuthenticationRequest;
import com.abb.abbouldering.dto.AuthenticationResponse;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock 
	private UserRepository mockUserRepo;
	@Mock 
	private PasswordEncoder mockPasswordEncoder;
	@Mock 
	private JwtService mockJwtService;
	@Mock 
	private AuthenticationManager mockAuthenticationManager;

	@InjectMocks
	private AuthenticationService authService;
	
	private RegisterRequest registerRequest;
	private AuthenticationRequest loginRequest;
	
	@BeforeEach
	void init() throws Exception {
		registerRequest = new RegisterRequest("firstName","lastName", "email@email.com","Password123");
		loginRequest = new AuthenticationRequest("email@email.com","Password123");
	}

	@Test
	void testAuthenticationService_isNotNull() {
		assertThat(authService).isNotNull();
	}
	
	@Test
	void testAuthenticationService_register_validRequestReturnsJwt() throws InvalidCredentialsException {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
		when(mockJwtService.generateToken(Mockito.any())).thenReturn("JwtToken");
		AuthenticationResponse response = authService.register(registerRequest);
		assertEquals("JwtToken", response.getToken());
	}
	
	@Test
	void testAuthenticationService_register_invalidEmailThrowsInvalidCredentialsException() throws InvalidCredentialsException {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
		registerRequest.setEmail("email");
		assertThrows(InvalidCredentialsException.class, () -> authService.register(registerRequest));
	}
	
	@Test
	void testAuthenticationService_register_invalidPasswordThrowsInvalidCredentialsException() throws InvalidCredentialsException {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
		registerRequest.setPassword("password");
		assertThrows(InvalidCredentialsException.class, () -> authService.register(registerRequest));
	}
	
	@Test
	void testAuthenticationService_register_emailAlreadyRegisterdThrowsInvalidCredentialsException() throws InvalidCredentialsException {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(new User()));
		assertThrows(InvalidCredentialsException.class, () -> authService.register(registerRequest));
	}
	
	@Test
	void testAuthenticationService_authenticate_validRequestReturnsJwt() {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(new User()));
		when(mockJwtService.generateToken(Mockito.any())).thenReturn("JwtToken");
		AuthenticationResponse response = authService.authenticate(loginRequest);
		assertEquals("JwtToken", response.getToken());
	}
	
	@Test
	void testAuthenticationService_authenticate_invalidRequestThrowsAuthenticationException() {
		when(mockAuthenticationManager.authenticate(Mockito.any())).thenThrow(BadCredentialsException.class);
		assertThrows(BadCredentialsException.class, () ->  authService.authenticate(loginRequest));
	}

	@Test
	void testAuthenticationService_authenticate_emailNotFoundThrowsUsernameNotFoundException() {
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () ->  authService.authenticate(loginRequest));
	}

}
