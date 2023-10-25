package com.abb.abbouldering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.exception.EventAlreadyExists;
import com.abb.abbouldering.exception.EventDoesNotExist;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.service.EventService;

@RestController("/api/v1/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@PostMapping
	public ResponseEntity<Event> handleAddEvent(@RequestBody Event event) throws EventAlreadyExists{
		return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(event));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Event> handleGetEventById(@PathVariable long id) throws EventDoesNotExist{
		return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(id));
	}
	
	@PutMapping()
	public ResponseEntity<Event> handleEditEvent(@RequestBody Event event) throws EventDoesNotExist{
		return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEvent(event));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Event> handleDeleteEvent(@PathVariable long id) throws EventDoesNotExist{
		eventService.deleteEventById(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
