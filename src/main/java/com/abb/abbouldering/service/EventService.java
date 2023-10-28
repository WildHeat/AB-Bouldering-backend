package com.abb.abbouldering.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.EventAlreadyExistsException;
import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.repository.EventRepository;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepo;
	
	public Event addEvent(Event event) throws EventAlreadyExistsException {
		if(eventRepo.existsById(event.getId())) {
			throw new EventAlreadyExistsException();
		}
		return eventRepo.save(event);
	}
	
	public void deleteEventById(long id) throws EventDoesNotExistException {
		if(!eventRepo.existsById(id)) throw new EventDoesNotExistException();
		
		eventRepo.deleteById(id);
	}
	
	public Event updateEvent(Event event) throws EventDoesNotExistException {
		if(eventRepo.existsById(event.getId())) throw new EventDoesNotExistException();
		return eventRepo.save(event);
	}
	
	public Event getEventById(long id) throws EventDoesNotExistException{
		Optional<Event> optionalEvent = eventRepo.findById(id);
		
		if(optionalEvent.isEmpty()) throw new EventDoesNotExistException();
		return optionalEvent.get();
	}
	
	public List<Event> getAllEvents(){
		return eventRepo.findAll();
	}
	
}
