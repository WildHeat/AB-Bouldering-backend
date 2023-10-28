package com.abb.abbouldering.exception;

public class EventDoesNotExistException extends Exception {

	private static final long serialVersionUID = -4103878150657738487L;

	public EventDoesNotExistException() {
		super("Event doesn't exist");
	}
	
}
