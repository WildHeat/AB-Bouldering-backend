package com.abb.abbouldering.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.abb.abbouldering.dto.AuthenticationRequest;
import com.abb.abbouldering.dto.AuthenticationResponse;
import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.dto.UserDto;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.UserRepository;
import com.abb.abbouldering.service.AuthenticationService;
import com.abb.abbouldering.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ControllerRestTest {

	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private EventRepository eventRepo;

	private RestTemplate rest;

	private String baseUrl;

	private Event event1;
	private Event event2;
	private Event event3;
	private Event event4;
	private Event event5;

	private EventDto eventDto;
	private User admin1;
	private User admin2;
	private User user;

	private UserDto userDto;

	@BeforeEach
	void init() throws Exception {
		baseUrl = "http://localhost:" + webServerAppCtxt.getWebServer().getPort();
		rest = new RestTemplate();

		eventRepo.deleteAll();
		userRepo.deleteAll();

		admin1 = new UserBuilder().email("admin1@asdf.com").firstName("admin1").lastName("last").role(Role.ADMIN)
				.password("$2a$10$BYbj9L2j6o.jGIEKDrPNQOBkwOWBWGnEPMV0bQw7bGSY0g40TyEpK").build();
		admin2 = new UserBuilder().email("admin2@asdf.com").firstName("admin2").lastName("last").role(Role.ADMIN)
				.password("$2a$10$BYbj9L2j6o.jGIEKDrPNQOBkwOWBWGnEPMV0bQw7bGSY0g40TyEpK").build();
		user = new UserBuilder().email("newUser@asdf.com").firstName("admin1").lastName("last").role(Role.USER)
				.password("$2a$10$BYbj9L2j6o.jGIEKDrPNQOBkwOWBWGnEPMV0bQw7bGSY0g40TyEpK").build();
		
		userRepo.save(admin1);
		userRepo.save(admin2);
		userRepo.save(user);

		event1 = new Event("Event1", "smallDescription1", "description1", 0.0, 10, LocalDateTime.now(), admin1,
				"imageUrl1");
		event2 = new Event("Event2", "smallDescription2", "description2", 1.0, 10, LocalDateTime.now(), admin1,
				"imageUrl2");
		event3 = new Event("Event3", "smallDescription3", "description3", 10.0, 10, LocalDateTime.now(), admin2,
				"imageUrl3");
		event4 = new Event("Event4", "smallDescription4", "description4", 21.0, 10, LocalDateTime.now(), admin2,
				"imageUrl4");
		event5 = new Event("Event5", "smallDescription5", "description5", 30.0, 10, LocalDateTime.now(), admin1,
				"imageUrl5");

		eventRepo.save(event1);
		eventRepo.save(event2);
		eventRepo.save(event3);
		eventRepo.save(event4);
		eventRepo.save(event5);

		eventDto = new EventDto(this.event1);
		userDto = new UserDto(user);
	}

	@Test
	void testAuthController_register_returns201ResponseAndJwt() {
		RegisterRequest request = new RegisterRequest("first", "last", "test@email.com", "Password123");
		ResponseEntity<AuthenticationResponse> response = rest.postForEntity(baseUrl + "/api/v1/auth/register", request,
				AuthenticationResponse.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertThat(response.getBody().getToken()).isNotBlank();
	}

	@Test
	void testAuthController_register_invalidEmailreturns406Response() {
		RegisterRequest request = new RegisterRequest("first", "last", "email123.com", "Password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("406 : \"Invalid email\"", e.getMessage());
			return;
		}
		fail("Invalid email was accepted");
	}

	@Test
	void testAuthController_register_invalidPasswordreturns406Response() {
		RegisterRequest request = new RegisterRequest("first", "last", "email123@email.com", "password");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("406 : \"Invalid password\"", e.getMessage());
			return;
		}
		fail("Invalid password was accepted");
	}

	@Test
	void testAuthController_register_emptyPasswordreturns406Response() {
		RegisterRequest request = new RegisterRequest("first", "last", "email123@email.com", "");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("406 : \"Invalid password\"", e.getMessage());
			return;
		}
		fail("Invalid password was accepted");
	}

	@Test
	void testAuthController_register_emptyFirstNamereturns403Response() {
		RegisterRequest request = new RegisterRequest("", "last", "email123@email.com", "Password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());
			return;
		}
		fail("Empty first name was accepted");
	}

	@Test
	void testAuthController_register_emptyLastNamereturns403Response() {
		RegisterRequest request = new RegisterRequest("first", "", "email123@email.com", "Password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());
			return;
		}
		fail("Empty first name was accepted");
	}

	@Test
	void testAuthController_register_emailAleadyExistsReturns406Response() {
		RegisterRequest request = new RegisterRequest("first", "asdf", user.getEmail(), "Password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/register", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("406 : \"Email is already registered\"", e.getMessage());
			return;
		}
		fail("Duplicate email was accepted");
	}

	@Test
	void testAuthController_login_returns200ResponseAndJwt() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
		ResponseEntity<AuthenticationResponse> response = rest.postForEntity(baseUrl + "/api/v1/auth/authenticate",
				loginRequest, AuthenticationResponse.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(response.getBody().getToken()).isNotBlank();
	}

	@Test
	void testAuthController_login_invalidCredentialsReturns403Response() {
		AuthenticationRequest request = new AuthenticationRequest("test@email.com", "password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/authenticate", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());			
			return;
		}
		fail("Invalid credentials was accepted");
	}

	@Test
	void testAuthController_login_incorrectPasswordReturns403Response() {
		AuthenticationRequest request = new AuthenticationRequest(user.getEmail(), "password123");
		try {
			rest.postForObject(baseUrl + "/api/v1/auth/authenticate", request, AuthenticationResponse.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());			
			return;
		}
		fail("Invalid credentials was accepted");
	}
	
	@Test
	void testEventController_handleGetAllEvents_returnsEventsArray() {
		EventDto[] events = rest.getForObject(baseUrl + "/api/v1/events/all", EventDto[].class);
		assertEquals(5, events.length);
	}

	@Test
	void testEventController_handleGetEventBy_returnsEventDto() {
		EventDto eventDto = rest.getForObject(baseUrl + "/api/v1/events/all/" + event1.getId(), EventDto.class);
		assertEquals(event1.getId(), eventDto.getId());
	}

	@Test
	void testEventController_handleGetEventBy_invalidEventIdThrows404EventDoesNotExistException() {
		try {
			rest.getForObject(baseUrl + "/api/v1/events/all/-12", EventDto.class);
		} catch (HttpClientErrorException e) {
			assertEquals("404 : \"Event doesn't exist\"", e.getMessage());
			return;
		}
		fail("Event should not exists inside the databasefor this test to function");
	}

	@Test
	void testEventController_handleGetTopEvents_returnsEventsArrayWith4Elements() {
		EventDto[] events = rest.getForObject(baseUrl + "/api/v1/events/all/top4", EventDto[].class);
		assertEquals(4, events.length);
	}

//	@Test
//	void testEventController_handleDeleteEventById_returnsOkStatus() {
//		 need to have the JWT in the header....NICE!!
//		ResponseEntity<String> response = rest.postForEntity(baseUrl + "/api/v1/auth/register", request, String.class);			
//		ResponseEntity response = rest.exchange(baseUrl + "/api/v1/events/2", HttpMethod.DELETE, null, void.class);
//		assertTrue(response.getStatusCode().is2xxSuccessful());
//	}

}
