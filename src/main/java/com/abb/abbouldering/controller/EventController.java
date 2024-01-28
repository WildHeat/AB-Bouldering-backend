package com.abb.abbouldering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.exception.EventAlreadyExistsException;
import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.service.EventService;


@RestController
@RequestMapping("/api/v1/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@GetMapping("/all")
	public ResponseEntity<List<EventDto>> handleGetAllEvents() {
		return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
	}

	@PostMapping
	public ResponseEntity<EventDto> handleAddEvent(@RequestBody EventDto eventDto) throws EventAlreadyExistsException, UserDoesNotExistException{
		return ResponseEntity.status(HttpStatus.CREATED).body(new EventDto(eventService.addEvent(eventDto)));
	}
	
	@GetMapping("/all/{id}")
	public ResponseEntity<EventDto> handleGetEventById(@PathVariable long id) throws EventDoesNotExistException{
		return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
	}
	
	@GetMapping("/all/top4")
	public ResponseEntity<List<EventDto>> handleGetTopEvents(){
		return ResponseEntity.status(HttpStatus.OK).body(eventService.getTopEvents());
	}
	
	@PutMapping
	public ResponseEntity<EventDto> handleEditEvent(@RequestBody EventDto eventDto) throws EventDoesNotExistException, UserDoesNotExistException{
		return ResponseEntity.status(HttpStatus.OK).body(new EventDto(eventService.updateEvent(eventDto)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Event> handleDeleteEvent(@PathVariable long id) throws EventDoesNotExistException{
		eventService.deleteEventById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/user/get-my-events")
	public ResponseEntity<List<EventDto>> handleGetMyEvents(@AuthenticationPrincipal User user){
		return ResponseEntity.status(HttpStatus.OK).body(eventService.getMyEvents(user));
	}
	
}
