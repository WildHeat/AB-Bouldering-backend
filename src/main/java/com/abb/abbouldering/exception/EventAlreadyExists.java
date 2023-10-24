package com.abb.abbouldering.exception;

public class EventAlreadyExists extends Exception {

	private static final long serialVersionUID = -6513013692964148313L;

	public EventAlreadyExists() {
		super("The event already exists");
	}
}
