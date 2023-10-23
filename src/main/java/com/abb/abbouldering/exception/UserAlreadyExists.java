package com.abb.abbouldering.exception;

public class UserAlreadyExists extends Exception {
	
	public UserAlreadyExists() {
		super("User already exists");
	}

}
