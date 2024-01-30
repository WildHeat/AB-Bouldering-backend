package com.abb.abbouldering.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.abb.abbouldering.config.JwtService;
import com.abb.abbouldering.dto.AuthenticationResponse;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerMockTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtService jwtService;
	
	@MockBean
	private AuthenticationService mockAuthService;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testAuthenticationController_register_returnsCreatedResponse() throws Exception {
		when(mockAuthService.register(Mockito.any())).thenReturn(new AuthenticationResponse("jwt"));
		ResultActions response = mockMvc.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterRequest())));
		response.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.token", CoreMatchers.is("jwt")));
	}

	@Test
	void testAuthenticationController_login_returnsOkResponse() throws Exception {
		when(mockAuthService.authenticate(Mockito.any())).thenReturn(new AuthenticationResponse("jwt"));
		ResultActions response = mockMvc.perform(post("/api/v1/auth/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new RegisterRequest())));
		response.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.token", CoreMatchers.is("jwt")));
	}

}
