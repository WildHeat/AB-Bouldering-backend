package com.abb.abbouldering.exception;

public class InvalidCredentialsException extends Exception {

	private static final long serialVersionUID = -3108020838852664918L;

	public InvalidCredentialsException(String message) {
		super(message);
	}
}
