package com.abb.abbouldering.exception;

public class UserDoesNotExists extends Exception {

	private static final long serialVersionUID = -7199363050512506637L;

	public UserDoesNotExists() {
		super("User doesn't exist");
	}
}
