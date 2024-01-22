package com.abb.abbouldering.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EventRepositoryTest {
	
	@Autowired
	private EventRepository eventRepo;
	
	@Autowired
	private UserRepository userRepo;

	@Test
	void testEventRepository_isNotNull() {
		assertThat(eventRepo).isNotNull();
	}
	
	@Test
	void testEventRepository_save_createsNewEvent(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		Event event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
	
		Event savedEvent = eventRepo.save(event);
		
		assertEquals(event, savedEvent);
	}
		
	@Test
	void testEventRepository_deleteById_deletesEvent(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		Event event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
		
		Event savedEvent = eventRepo.save(event);
		assertTrue(eventRepo.findById(savedEvent.getId()).isPresent());
		eventRepo.deleteById(savedEvent.getId());
		assertFalse(eventRepo.findById(event.getId()).isPresent());
	}
	
	
	@Test
	void testEventRepository_findAllEvents_returnsAllEvents(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		
		Event event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
		Event savedEvent = eventRepo.save(event);
		assertTrue(eventRepo.findAll().contains(savedEvent));
	}
	
	
	@Test
	void testEventRepository_findById_returnsEvent(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		
		Event event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
		eventRepo.save(event);
		assertTrue(eventRepo.findById(event.getId()).isPresent());
	}
	
	@Test
	void testEventRepository_findByOrganiser_returnsAllEventsForOrganiser(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		
		Event event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
		Event event2 = new Event("title2", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), organiser, "imageUrl");
		eventRepo.save(event);
		eventRepo.save(event2);
		assertTrue(eventRepo.findByOrganiser(organiser).contains(event));
		assertTrue(eventRepo.findByOrganiser(organiser).contains(event2));
		assertTrue(eventRepo.findByOrganiser(organiser).size() == 2);
	}
	
	@Test
	void testEventRepository_findFirst4ByOrderByDateDesc_returnsOnly4Event(){
		User organiser = new UserBuilder()
				.email("email@email.com")
				.password("Password123")
				.role(Role.ADMIN)
				.firstName("first")
				.lastName("last")
				.build();
		
		userRepo.save(organiser);	
		
		Event event1 = new Event("title1", "smallDescription", "description", 23.0, 1, LocalDateTime.now().plusYears(4), organiser, "imageUrl");
		Event event2 = new Event("title2", "smallDescription", "description", 23.0, 1, LocalDateTime.now().plusYears(4), organiser, "imageUrl");
		Event event3 = new Event("title3", "smallDescription", "description", 23.0, 1, LocalDateTime.now().plusYears(4), organiser, "imageUrl");
		Event event4 = new Event("title4", "smallDescription", "description", 23.0, 1, LocalDateTime.now().plusYears(4), organiser, "imageUrl");
		eventRepo.save(event1);
		eventRepo.save(event2);
		eventRepo.save(event3);
		eventRepo.save(event4);
		
		assertTrue(eventRepo.findFirst4ByOrderByDateDesc().contains(event1));
		assertTrue(eventRepo.findFirst4ByOrderByDateDesc().contains(event2));
		assertTrue(eventRepo.findFirst4ByOrderByDateDesc().contains(event3));
		assertTrue(eventRepo.findFirst4ByOrderByDateDesc().contains(event4));
		assertTrue(eventRepo.findByOrganiser(organiser).size() == 4);
	
	}

}
