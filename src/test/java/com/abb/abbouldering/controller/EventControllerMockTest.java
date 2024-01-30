package com.abb.abbouldering.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDateTime;
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
import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class EventControllerMockTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private EventService mockEventService;
	
	@MockBean
	private JwtService jwtService; 
	
	private Event event;
	private EventDto eventDto;
	private User organiser;

	@BeforeEach
	void init() throws Exception {
		organiser = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();
		event = new Event("Event", "smallDescription", "description", 10.0, 10, LocalDateTime.now(), organiser,
				"imageUrl");
		eventDto = new EventDto(this.event);
	}

	@Test
	void testEventController_handleGetAllEvents_returnsOkStatus() throws Exception {
		when(mockEventService.getAllEvents()).thenReturn(new ArrayList<EventDto>());
		ResultActions response = mockMvc.perform(get("/api/v1/events/all"));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void testEventController_handleAddEvent_returnsEventDtoOkCreatedResponse() throws Exception {
		when(mockEventService.addEvent(Mockito.any(EventDto.class))).thenReturn(event);
		ResultActions response = mockMvc.perform(post("/api/v1/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)));
		
		response.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) eventDto.getId())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(eventDto.getTitle())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.smallDescription", CoreMatchers.is(eventDto.getSmallDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(eventDto.getDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(eventDto.getPrice())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.maxSize", CoreMatchers.is(eventDto.getMaxSize())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.date", CoreMatchers.containsString(eventDto.getDate().toString().substring(0, 16))))
		.andExpect(MockMvcResultMatchers.jsonPath("$.organiser", CoreMatchers.is(eventDto.getOrganiser())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.spacesLeft", CoreMatchers.is(eventDto.getSpacesLeft())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl", CoreMatchers.is(eventDto.getImageUrl())));
	}
	
	@Test
	void testEventController_handleGetEventById_returnsEventDtoOkStatus() throws Exception {
		when(mockEventService.getEventById(Mockito.anyLong())).thenReturn(eventDto);
		ResultActions response = mockMvc.perform(get("/api/v1/events/all/"+ eventDto.getId()));
		
		response.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) eventDto.getId())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(eventDto.getTitle())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.smallDescription", CoreMatchers.is(eventDto.getSmallDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(eventDto.getDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(eventDto.getPrice())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.maxSize", CoreMatchers.is(eventDto.getMaxSize())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.date", CoreMatchers.containsString(eventDto.getDate().toString().substring(0, 16))))
		.andExpect(MockMvcResultMatchers.jsonPath("$.organiser", CoreMatchers.is(eventDto.getOrganiser())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.spacesLeft", CoreMatchers.is(eventDto.getSpacesLeft())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl", CoreMatchers.is(eventDto.getImageUrl())));
	}
	
	@Test
	void testEventController_handleGetTopEvents_returnsEventDtoOkStatus() throws Exception {
		when(mockEventService.getTopEvents()).thenReturn(new ArrayList<EventDto>());
		ResultActions response = mockMvc.perform(get("/api/v1/events/all/top4"));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	void testEventController_handleEditEvent_returnsEventDtoOkStatus() throws Exception {
		when(mockEventService.updateEvent(Mockito.any(EventDto.class))).thenReturn(event);
		ResultActions response = mockMvc.perform(put("/api/v1/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventDto)));
		
		response.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) eventDto.getId())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(eventDto.getTitle())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.smallDescription", CoreMatchers.is(eventDto.getSmallDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(eventDto.getDescription())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(eventDto.getPrice())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.maxSize", CoreMatchers.is(eventDto.getMaxSize())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.date", CoreMatchers.containsString(eventDto.getDate().toString().substring(0, 16))))
		.andExpect(MockMvcResultMatchers.jsonPath("$.organiser", CoreMatchers.is(eventDto.getOrganiser())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.spacesLeft", CoreMatchers.is(eventDto.getSpacesLeft())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl", CoreMatchers.is(eventDto.getImageUrl())));
	}
	
	@Test
	void testEventController_handleDeleteEvent_returnsOkStatus() throws Exception {
		ResultActions response = mockMvc.perform(delete("/api/v1/events/"+ event.getId()));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void testEventController_handleGetMyEventst_returnsOkStatus() throws Exception {
		when(mockEventService.getMyEvents(Mockito.any())).thenReturn(null);
		ResultActions response = mockMvc.perform(get("/api/v1/events/user/get-my-events"));
		response.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
