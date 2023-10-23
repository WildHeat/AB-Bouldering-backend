package com.abb.abbouldering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abb.abbouldering.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

}
