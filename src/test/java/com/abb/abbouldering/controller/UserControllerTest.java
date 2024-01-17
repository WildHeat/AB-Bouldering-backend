package com.abb.abbouldering.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest
class UserControllerTest {

	@Autowired
	private UserController userController;
	
//	@Autowired
//	private TestRestTemplate restTemplate;

	@Test
	void testUserControllerIsNotNull() throws Exception {
		assertThat(userController).isNotNull();
	}
	
	@Test
	void testingsomething() {
//		assertThat(this.restTemplate.getForObject("http://localhost:8080/api/v1/events/all",
//				String.class)).contains("Hello, World");
	}

}
