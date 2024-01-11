package com.abb.abbouldering.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class SessionWithUser {

	@Id
	private String sessionId;
	@ManyToOne(optional = false)
	private User user;
	@ManyToOne(optional = false)
	private Event event;

	public SessionWithUser() {
	}

	public SessionWithUser(String sessionId, User user, Event event) {
		super();
		this.sessionId = sessionId;
		this.user = user;
		this.event = event;
	}

	public String getId() {
		return sessionId;
	}

	public void setId(String id) {
		this.sessionId = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
