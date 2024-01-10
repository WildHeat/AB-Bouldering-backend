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

	public SessionWithUser(String sessionId, User user) {
		super();
		this.sessionId = sessionId;
		this.user = user;
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

}
