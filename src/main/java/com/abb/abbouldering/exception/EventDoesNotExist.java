package com.abb.abbouldering.exception;

public class EventDoesNotExist extends Exception {

	private static final long serialVersionUID = -4103878150657738487L;

	public EventDoesNotExist() {
		super("Event doesn't exist");
	}
	
}
