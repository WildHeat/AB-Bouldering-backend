package com.abb.abbouldering.exception;

public class UserAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 659734964519738761L;

	public UserAlreadyExistsException() {
		super("User already exists");
	}

}
