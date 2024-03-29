package com.abb.abbouldering.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.abb.abbouldering.exception.EventAlreadyExistsException;
import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.EventIsFullyBookedException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEventException;

@ControllerAdvice
public class EventExceptionHandler {

	@ExceptionHandler(value = EventAlreadyExistsException.class)
	public ResponseEntity<String> handleEventAlreadyExistsException(EventAlreadyExistsException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler(value = EventDoesNotExistException.class)
	public ResponseEntity<String> handleEventDoesNotExistException(EventDoesNotExistException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
	
	@ExceptionHandler(value = UserIsAlreadySignedUpForEventException.class)
	public ResponseEntity<String> handleUserIsAlreadySignedUpForEventException(UserIsAlreadySignedUpForEventException e){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	} 
	
	@ExceptionHandler(value = EventIsFullyBookedException.class)
	public ResponseEntity<String> handleEventIsFullyBookedException(EventIsFullyBookedException e){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}
}
