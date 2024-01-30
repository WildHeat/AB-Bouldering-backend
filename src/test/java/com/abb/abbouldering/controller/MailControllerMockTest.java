package com.abb.abbouldering.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.abb.abbouldering.dto.ContactFormDto;
import com.abb.abbouldering.service.MailSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = MailController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MailControllerMockTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtService jwtService;
	
	@MockBean
	private MailSenderService mockMailService;


	@Test
	void testMailController_contactEmail_returnsOkResponse() throws Exception {
		ContactFormDto contactForm = new ContactFormDto("email", "subject", "message", "name");
		ResultActions response = mockMvc.perform(post("/api/v1/mail").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(contactForm)));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
