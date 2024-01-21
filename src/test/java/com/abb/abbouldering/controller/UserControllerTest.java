package com.abb.abbouldering.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.PageAttributes.MediaType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;

@SpringBootTest
class UserControllerTest {

	@Autowired
	private UserController userController;
	
//	@Autowired 
	WebTestClient client =
	MockMvcWebTestClient.bindToController(new UserController()).build();

	@Test
	void testUserControllerIsNotNull() throws Exception {
		assertThat(userController).isNotNull();
	}
	
	@Test
	void testingsomething() {
		User user = new UserBuilder()
				.email("testEmail")
				.password("Password123")
				.firstName("asdfasdf")
				.lastName("asdfasdf")
				.build();
		
		client.post().uri("/api/v1/users")
		//.body(user, User.class)
		.exchange().expectStatus().isCreated();
	}

}
