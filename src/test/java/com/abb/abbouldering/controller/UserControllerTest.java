package com.abb.abbouldering.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;

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
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private UserService mockUserService; 
	@MockBean
	private JwtService jwtService; 
	
	private User user;
	
	@BeforeEach
	void init() {
		user = new UserBuilder()
				.email("testEmail")
				.password("Password123")
				.firstName("first")
				.lastName("last")
				.role(Role.ADMIN)
				.build();
	}
	
	@Test
	void testUserController_getUserById_returnUserDto() throws Exception {
		when(mockUserService.getUserById(Mockito.anyLong())).thenReturn(user);
		ResultActions response = mockMvc.perform(get("/api/v1/users/"+ user.getId()));
		response.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(user.getFirstName())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(user.getLastName())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(user.getEmail())));
	}	
	
	@Test
	void testUserController_handleDeleteUser_returnOkResponse() throws Exception {
		ResultActions response = mockMvc.perform(delete("/api/v1/users/"+ user.getId()));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void testUserController_addNewAdmin_returnUser() throws Exception {
		when(mockUserService.addNewAdmin(Mockito.any(RegisterRequest.class))).thenReturn(user);
		RegisterRequest register = new RegisterRequest(user.getFirstName(),user.getLastName(),user.getEmail(), user.getPassword()); 
		
		ResultActions response = mockMvc.perform(post("/api/v1/users/new-admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(register)));
		response.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(user.getFirstName())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(user.getLastName())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(user.getEmail())));
	}
	
	@Test
	void testUserController_handleGetAllAdminNames_returnOkResponse() throws Exception {
		ArrayList<String> listOfAdminNames = new ArrayList<String>();
		listOfAdminNames.add("Admin 1");
		listOfAdminNames.add("Admin 2");
		listOfAdminNames.add("Admin 3");
		
		ResultActions response = mockMvc.perform(get("/api/v1/users/get-all-admin"));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
} 
