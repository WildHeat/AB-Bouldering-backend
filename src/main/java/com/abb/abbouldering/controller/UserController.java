package com.abb.abbouldering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserAlreadyExistsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.service.UserService;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
	
	@Autowired
	private UserService userService; 
	
	@PostMapping
	public ResponseEntity<User> handleAddUser(@RequestBody User user) throws UserAlreadyExistsException, InvalidCredentialsException{
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user));
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<User> handleDeleteUser(@PathVariable long id) throws UserDoesNotExistException {
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> handleGetUser(@PathVariable long id) throws UserDoesNotExistException{
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
	}

	@PutMapping
	public ResponseEntity<User> handleEditUser(@RequestBody User user) throws UserDoesNotExistException, InvalidCredentialsException{
		return ResponseEntity.status(HttpStatus.OK).body(userService.editUser(user));
	}
}
