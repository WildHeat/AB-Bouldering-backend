package com.abb.abbouldering.exception;

public class InvalidEmailException extends Exception {

	private static final long serialVersionUID = 3432643317799544904L;

	public InvalidEmailException(String message) {
		super(message);
	}
}
