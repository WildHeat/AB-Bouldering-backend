package com.abb.abbouldering.exception;

public class EventAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -6513013692964148313L;

	public EventAlreadyExistsException() {
		super("The event already exists");
	}
}
