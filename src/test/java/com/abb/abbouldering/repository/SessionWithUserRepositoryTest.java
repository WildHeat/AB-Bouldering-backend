package com.abb.abbouldering.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.SessionWithUser;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SessionWithUserRepositoryTest {

	@Autowired
	private SessionWithUserRepository sessionRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EventRepository eventRepo;

	private SessionWithUser session;
	private User organiser;
	private User user;
	private Event event;

	@BeforeEach
	void init() {
		organiser = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();
		userRepo.save(organiser);
		event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser,
				"imageUrl");
		eventRepo.save(event);
		user = new UserBuilder().email("email@email.com").password("Password123").role(Role.USER).firstName("first")
				.lastName("last").build();
		userRepo.save(user);
		session = new SessionWithUser("SessionId", user, event);
	}

	@Test
	void testSessionWithUserRepository_isNotNull() {
		assertThat(sessionRepo).isNotNull();
	}

	// save
	@Test
	void testSessionWithUserRepository_save_returnsSession() {
		SessionWithUser savedSession = sessionRepo.save(session);
		assertEquals(session.getId(), savedSession.getId());
		assertEquals(session.getEvent(), savedSession.getEvent());
		assertEquals(session.getUser(), savedSession.getUser());
	}

	@Test
	void testSessionWithUserRepository_findById_returnsSession() {
		sessionRepo.save(session);
		Optional<SessionWithUser> optionalSession = sessionRepo.findById(session.getId());
		assertTrue(optionalSession.isPresent());
		SessionWithUser actualSession = optionalSession.get();
		assertEquals(session.getId(), actualSession.getId());
		assertEquals(session.getEvent(), actualSession.getEvent());
		assertEquals(session.getUser(), actualSession.getUser());
	}

	@Test
	void testSessionWithUserRepository_deleteById_deletesSession() {
		sessionRepo.save(session);
		sessionRepo.deleteById(session.getId());
		assertFalse(sessionRepo.findById(session.getId()).isPresent());
	}
	
	@Test
	void testEventRepo_deleteById_deletesSession() {
		sessionRepo.save(session);
		eventRepo.deleteById(event.getId());
		assertFalse(eventRepo.findById(event.getId()).isPresent());
		
	}
}
