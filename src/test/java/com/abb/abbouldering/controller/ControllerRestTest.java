package com.abb.abbouldering.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.abb.abbouldering.dto.AuthenticationRequest;
import com.abb.abbouldering.dto.AuthenticationResponse;
import com.abb.abbouldering.dto.EditUserDto;
import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.dto.UserDto;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.SessionWithUserRepository;
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
	private EventRepository eventRepo;

	@Autowired
	private SessionWithUserRepository sessionRepo;
	
	private RestTemplate rest;

	private String baseUrl;

	private Event event1;
	private Event event2;
	private Event event3;
	private Event event4;
	private Event event5;

	private User admin1;
	private User admin2;
	private User user;

	private UserDto userDto;

	@BeforeEach
	void init() throws Exception {
		baseUrl = "http://localhost:" + webServerAppCtxt.getWebServer().getPort();
		rest = new RestTemplate();

		sessionRepo.deleteAll();
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
		event4 = new Event("Event4", "smallDescription4", "description4", 0.0, 10, LocalDateTime.now(), admin2,
				"imageUrl4");
		event5 = new Event("Event5", "smallDescription5", "description5", 30.0, 10, LocalDateTime.now(), admin1,
				"imageUrl5");

		eventRepo.save(event1);
		eventRepo.save(event2);
		eventRepo.save(event3);
		eventRepo.save(event4);
		eventRepo.save(event5);
		
		event1.addUserToEvent(user);
		eventRepo.save(event1);
		

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
	/////////////////////////////////
	///////////////////////////////

	// Events

	/////////////////////////////////
	////////////////////////////

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

	@Test
	void testEventController_handleAddEvent_returns201Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());

		EventDto eventDto = new EventDto(event1);
		eventDto.setId(0l);
		eventDto.setTitle("Test New Event");

		HttpEntity<?> request = new HttpEntity<>(eventDto, headers);
		ResponseEntity<EventDto> response = rest.exchange(baseUrl + "/api/v1/events", HttpMethod.POST, request,
				EventDto.class);

		assertEquals(eventDto.getTitle(), response.getBody().getTitle());
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void testEventController_handleAddEvent_nonAdminReturns403() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		EventDto eventDto = new EventDto(event1);
		eventDto.setTitle("Test New Event");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());

		HttpEntity<?> request = new HttpEntity<>(eventDto, headers);
		try {
			rest.exchange(baseUrl + "/api/v1/events", HttpMethod.POST, request, EventDto.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());
		}
	}

	@Test
	void testEventController_handleDeleteEventById_adminReturns200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		assertFalse(eventRepo.findById(event1.getId()).isEmpty());

		rest.exchange(baseUrl + "/api/v1/events/" + event1.getId(), HttpMethod.DELETE, request, void.class);

		assertTrue(eventRepo.findById(event1.getId()).isEmpty());
	}

	@Test
	void testEventController_handleDeleteEventById_invalidIdReturns404Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		long fakeId = 1111l;

		assertTrue(eventRepo.findById(fakeId).isEmpty());

		try {
			rest.exchange(baseUrl + "/api/v1/events/" + fakeId, HttpMethod.DELETE, request, void.class);
		} catch (Exception e) {
			assertEquals("404 : \"Event doesn't exist\"", e.getMessage());
			return;
		}
		fail("Should have thrown an exception");
	}

	@Test
	void testEventController_handleDeleteEventById_nonAdminReturns403Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		try {
			rest.exchange(baseUrl + "/api/v1/events/" + event1.getId(), HttpMethod.DELETE, request, void.class);
		} catch (Exception e) {
			assertEquals("403 : [no body]", e.getMessage());
			return;
		}
		fail();
	}

	@Test
	void testEventController_handleEditEvent_returns201Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());

		String newTitle = "Test new title";
		String newDescription = "test new description";
		
		EventDto eventDto = new EventDto(event1);
		eventDto.setTitle(newTitle);
		eventDto.setDescription(newDescription);

		HttpEntity<?> request = new HttpEntity<>(eventDto, headers);
		rest.exchange(baseUrl + "/api/v1/events", HttpMethod.PUT, request, EventDto.class);
		
		Event editedEvent = eventRepo.findById(event1.getId()).get();
		
		assertEquals(newTitle, editedEvent.getTitle());		
		assertEquals(newDescription, editedEvent.getDescription());		
	}
	
	@Test
	void testEventController_handleGetMyEvents_adminReturnsEventsTheyAreOrganising() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		ResponseEntity<EventDto[]> response = rest.exchange(baseUrl + "/api/v1/events/user/get-my-events", HttpMethod.GET, request, EventDto[].class);
		assertEquals(3, response.getBody().length);
	}

	@Test
	void testEventController_handleGetMyEvents_userReturnsTheirBookedEvents() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<EventDto[]> response = rest.exchange(baseUrl + "/api/v1/events/user/get-my-events", HttpMethod.GET, request, EventDto[].class);
		assertEquals(1, response.getBody().length);
	}
	
	//////////////////////
	/////////////////
	// User
	/////////////////
	//////////////////////
	
	@Test
	void testUserController_handleGetUser_returnsUserDtoAnd200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<UserDto> response = rest.exchange(baseUrl + "/api/v1/users/get-user", HttpMethod.GET, request, UserDto.class);
		UserDto retrievedUser = response.getBody();
		assertEquals(admin1.getId(), retrievedUser.getId());
		assertEquals(admin1.getEmail(), retrievedUser.getEmail());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testUserController_handleGetAllAdminNames_returnsListAnd200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<String[]> response = rest.exchange(baseUrl + "/api/v1/users/get-all-admin", HttpMethod.GET, request, String[].class);
		String[] adminNames = response.getBody();
		
		assertEquals(2, adminNames.length);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testUserController_handleEditUser_returns200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		
		EditUserDto editUserDto = new EditUserDto(admin1.getId(), "editedFirst", "editedLast", admin1.getEmail(), "Password1234", "Password123");
		
		HttpEntity<?> request = new HttpEntity<>(editUserDto, headers);
		
		ResponseEntity<UserDto> response = rest.exchange(baseUrl + "/api/v1/users", HttpMethod.PUT, request, UserDto.class);
		UserDto editedUser = response.getBody();
		
		assertEquals(admin1.getId(), editedUser.getId());
		assertEquals("editedFirst", editedUser.getFirstName());
		assertEquals("editedLast", editedUser.getLastName());
		assertEquals(admin1.getEmail(), editedUser.getEmail());
	}
	
	@Test
	void testUserController_handleUserById_returnsUserAnd200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<UserDto> response = rest.exchange(baseUrl + "/api/v1/users/" + user.getId(), HttpMethod.GET, request, UserDto.class);
		UserDto retrievedUser = response.getBody();
		
		assertEquals(user.getId(), retrievedUser.getId());
		assertEquals(user.getEmail(), retrievedUser.getEmail());
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testUserController_handleDeleteUserById_returnsUserAnd200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<Void> response = rest.exchange(baseUrl + "/api/v1/users/" + user.getId(), HttpMethod.DELETE, request, void.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	///////////////////
	////////////////
	//Stripe
	////////////////
	/////////////////
	
	@Test
	void testStripeController_addUserToEvent_returnUrlAnd200Response() {
		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
				AuthenticationResponse.class);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + loginResponse.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = rest.exchange(baseUrl + "/api/v1/stripe/all/" + event3.getId(), HttpMethod.GET, request, String.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotEquals(null, response.getBody());
	}
	
//	@Test
//	void testStripeController_addUserToEvent_return200ResponseAndAddsUserToEvent() {
//		AuthenticationRequest loginRequest = new AuthenticationRequest(user.getEmail(), "Password123");
//		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
//				AuthenticationResponse.class);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Authorization", "Bearer " + loginResponse.getToken());
//		HttpEntity<?> request = new HttpEntity<>(headers);
//		
//		ResponseEntity<String> response = rest.exchange(baseUrl + "/api/v1/stripe/all/" + event4.getId(), HttpMethod.GET, request, String.class);
//	
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals(null, response.getBody());
//	}
//
//	@Test
//	void testEventControllerStripeController_deleteEventById_return200ResponseEvenWithSessionWithUserEntity() {
//		AuthenticationRequest loginRequest = new AuthenticationRequest(admin1.getEmail(), "Password123");
//		AuthenticationResponse loginResponse = rest.postForObject(baseUrl + "/api/v1/auth/authenticate", loginRequest,
//				AuthenticationResponse.class);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Authorization", "Bearer " + loginResponse.getToken());
//		HttpEntity<?> request = new HttpEntity<>(headers);
//		
//		ResponseEntity<String> response = rest.exchange(baseUrl + "/api/v1/stripe/all/" + event2.getId(), HttpMethod.GET, request, String.class);
//	
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		
//		rest.exchange(baseUrl + "/api/v1/events/" + event2.getId(), HttpMethod.DELETE, request, void.class);
//
//		assertTrue(eventRepo.findById(event2.getId()).isEmpty());
//		
//	}

		
}
