package com.abb.abbouldering.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserAlreadyExistsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;

@ControllerAdvice
public class UserExceptionHandler {

	@ExceptionHandler(value = UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler(value = UserDoesNotExistException.class)
	public ResponseEntity<String> handleUserDoesNotExistException(UserDoesNotExistException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}

	@ExceptionHandler(value = InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException e) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
	}

	@ExceptionHandler(value = UsernameNotFoundException.class)
	public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
}
