package com.abb.abbouldering.exception;

public class UserIsAlreadySignedUpForEventException extends Exception {
	private static final long serialVersionUID = 6806963702430983295L;

	public UserIsAlreadySignedUpForEventException() {
		super("User is already signed up for event");
	}

}
