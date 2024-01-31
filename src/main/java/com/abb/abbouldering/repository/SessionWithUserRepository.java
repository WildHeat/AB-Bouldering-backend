package com.abb.abbouldering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.SessionWithUser;

public interface SessionWithUserRepository extends JpaRepository<SessionWithUser, String>{
	
	List<SessionWithUser> findByEvent(Event event);
	
}
