package com.abb.abbouldering.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.exception.EventAlreadyExistsException;
import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.EventIsFullyBookedException;
import com.abb.abbouldering.exception.InvalidEmailException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEventException;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

	@Mock
	private MailSenderService mockMailService;
	@Mock
	private EventRepository mockEventRepo;
	@Mock
	private UserRepository mockUserRepo;
	@InjectMocks
	private EventService eventService;

	private User user1;
	private User user2;
	private User organiser;
	private Event event;
	private EventDto eventDto;

	@Test
	void testEventService_eventServiceIsNotNull() {
		assertThat(eventService).isNotNull();
	}

	@BeforeEach
	void init() {

		this.organiser = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();
		this.event = new Event("Event", "smallDescription", "description", 10.0, 10, LocalDateTime.now(), organiser,
				"imageUrl");
		this.eventDto = new EventDto(event);
		this.user1 = new UserBuilder().email("user@email.com").password("Password123").role(Role.USER)
				.firstName("first").lastName("last").build();
		this.user2 = new UserBuilder().email("user2@email.com").password("2Password123").role(Role.USER)
				.firstName("2first").lastName("2last").build();
	}

	@Test
	void testEventService_addEvent_returnsEvent() throws EventAlreadyExistsException, UserDoesNotExistException {
		when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(organiser));
		EventDto eventDto = new EventDto(event);
		eventService.addEvent(eventDto);
		verify(mockEventRepo).save(Mockito.any(Event.class));
	}

	@Test
	void testEventService_deleteEventBy_runsEventRepoDeleteById() throws EventDoesNotExistException {
		when(mockEventRepo.existsById(Mockito.anyLong())).thenReturn(true);
		eventService.deleteEventById(1l);
		verify(mockEventRepo).deleteById(1l);
	}

	@Test
	void testEventService_deleteEventBy_invalidEventThrowsEventDoesNotExistException() {
		when(mockEventRepo.existsById(Mockito.anyLong())).thenReturn(false);
		assertThrows(EventDoesNotExistException.class, () -> eventService.deleteEventById(1l));
	}

	@Test
	void testEventService_getEventById_returnsEvent() throws EventDoesNotExistException {
		when(mockEventRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
		EventDto expectedEventDto = new EventDto(event);
		EventDto actualEventDto = eventService.getEventById(event.getId());

		assertEquals(expectedEventDto.getId(), actualEventDto.getId());
		assertEquals(expectedEventDto.getDescription(), actualEventDto.getDescription());
		assertEquals(expectedEventDto.getSmallDescription(), actualEventDto.getSmallDescription());
		assertEquals(expectedEventDto.getDate(), actualEventDto.getDate());
		assertEquals(expectedEventDto.getImageUrl(), actualEventDto.getImageUrl());
		assertEquals(expectedEventDto.getMaxSize(), actualEventDto.getMaxSize());
		assertEquals(expectedEventDto.getPrice(), actualEventDto.getPrice());
		assertEquals(expectedEventDto.getOrganiser(), actualEventDto.getOrganiser());
		assertEquals(expectedEventDto.getSpacesLeft(), actualEventDto.getSpacesLeft());
		assertEquals(expectedEventDto.getTitle(), actualEventDto.getTitle());

	}

	@Test
	void testEventService_getEventById_invalidIdThrowsEventDoesNotExistException(){
		when(mockEventRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(EventDoesNotExistException.class, () ->  eventService.getEventById(1l));
	}

	@Test
	void testEventService_updateEvent_validDtoReturnsUpdatedEvent() throws EventDoesNotExistException, UserDoesNotExistException{
		when(mockEventRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(event));
		when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(organiser));
		eventService.updateEvent(eventDto);
		verify(mockEventRepo).save(Mockito.any(Event.class));
	}

	@Test
	void testEventService_updateEvent_invalidIdThrowsEventDoesNotExistException(){
		when(mockEventRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(EventDoesNotExistException.class, () -> eventService.updateEvent(eventDto));
	}

	@Test
	void testEventService_getAllEvents_returnsListOfEvents() {
		ArrayList<Event> listOfEvents = new ArrayList<>();
		listOfEvents.add(event);
		listOfEvents.add(event);
		listOfEvents.add(event);

		when(mockEventRepo.findAll()).thenReturn(listOfEvents);
		List<EventDto> finalList = eventService.getAllEvents();
		assertEquals(3, finalList.size());
	}

	@Test
	void testEventService_addUserToEvent_returnsEventDto() throws EventDoesNotExistException, UserIsAlreadySignedUpForEventException, EventIsFullyBookedException, InvalidEmailException
			{
		when(mockEventRepo.save(Mockito.any(Event.class))).thenReturn(event);
		assertThat(eventService.addUserToEvent(user1, event)).isNotNull();
		verify(mockEventRepo).save(event);
		verify(mockMailService).sendBookingConfirmationEmail(user1, event);
	}

	@Test
	void testEventService_addUserToEvent_ThrowsExceptionIfEventIsFullyBooked() {
		event.setMaxSize(1);
		event.addUserToEvent(user1);

		assertThrows(EventIsFullyBookedException.class, () -> eventService.addUserToEvent(user2, event));
	}

	@Test
	void testEventService_addUserToEvent_ThrowsExceptionIfUserIsAlreadyBooked() {
		event.addUserToEvent(user1);
		assertThrows(UserIsAlreadySignedUpForEventException.class, () -> eventService.addUserToEvent(user1, event));
	}

}
