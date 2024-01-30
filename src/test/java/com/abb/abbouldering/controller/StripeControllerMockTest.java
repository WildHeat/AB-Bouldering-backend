package com.abb.abbouldering.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.service.StripeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = StripeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class StripeControllerMockTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtService jwtService;
	
	@MockBean
	private StripeService mockStripeService;

	@BeforeEach
	void init() throws Exception {
	}

	@Test
	void testStripeController_handleNewEventNotification_returnsOkResponse() throws Exception {
		ResultActions response = mockMvc.perform(post("/api/v1/stripe/event")
				.header("Stripe-Signature", "signature")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString("event")));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void testStripeController_addUserToEvent_returnsOkResponse() throws Exception {
		when(mockStripeService.handleCreateCheckoutSession(Mockito.any(User.class), Mockito.anyLong())).thenReturn("url");
		ResultActions response = mockMvc.perform(get("/api/v1/stripe/all/2"));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
