package com.abb.abbouldering.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.EventAlreadyExists;
import com.abb.abbouldering.exception.EventDoesNotExist;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.repository.EventRepository;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepo;
	
	public Event addEvent(Event event) throws EventAlreadyExists {
		if(eventRepo.existsById(event.getId())) {
			throw new EventAlreadyExists();
		}
		return eventRepo.save(event);
	}
	
	public void deleteEventById(long id) throws EventDoesNotExist {
		if(!eventRepo.existsById(id)) throw new EventDoesNotExist();
		
		eventRepo.deleteById(id);
	}
	
	public Event updateEvent(Event event) throws EventDoesNotExist {
		if(eventRepo.existsById(event.getId())) throw new EventDoesNotExist();
		return eventRepo.save(event);
	}
	
	public Event getEventById(long id) throws EventDoesNotExist{
		Optional<Event> optionalEvent = eventRepo.findById(id);
		
		if(optionalEvent.isEmpty()) throw new EventDoesNotExist();
		return optionalEvent.get();
	}
	
	public List<Event> getAllEvents(){
		return eventRepo.findAll();
	}
	
}
