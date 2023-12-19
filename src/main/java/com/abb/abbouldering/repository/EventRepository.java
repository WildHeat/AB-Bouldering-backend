package com.abb.abbouldering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.User;

public interface EventRepository extends JpaRepository<Event, Long>{
	
	List<Event> findByOrganiser(User organiser);
	
	List<Event> findFirst4ByOrderByDateDesc();
	
}
