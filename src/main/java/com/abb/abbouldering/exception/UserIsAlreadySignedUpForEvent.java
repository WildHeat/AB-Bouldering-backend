package com.abb.abbouldering.exception;

public class UserIsAlreadySignedUpForEvent extends Exception {
	private static final long serialVersionUID = 6806963702430983295L;

	public UserIsAlreadySignedUpForEvent() {
		super("User is already signed up for event");
	}

}
