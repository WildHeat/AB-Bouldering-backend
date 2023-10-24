package com.abb.abbouldering.exception;

public class UserAlreadyExists extends Exception {

	private static final long serialVersionUID = 659734964519738761L;

	public UserAlreadyExists() {
		super("User already exists");
	}

}
