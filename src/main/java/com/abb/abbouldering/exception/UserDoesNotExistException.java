package com.abb.abbouldering.exception;

public class UserDoesNotExistException extends Exception {

	private static final long serialVersionUID = -7199363050512506637L;

	public UserDoesNotExistException() {
		super("User doesn't exist");
	}
}
