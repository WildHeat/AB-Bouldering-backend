package com.abb.abbouldering.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.dto.EditUserDto;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.dto.UserDto;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	@Autowired
	private UserService userService; 
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> handleDeleteUser(@PathVariable long id) throws UserDoesNotExistException {
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/get-all-admin")
	public ResponseEntity<ArrayList<String>> handleGetAllAdminNames(){
		return ResponseEntity.status(HttpStatus.OK).body(userService.getAllAdminNames());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserDto> handleGetUserById(@PathVariable long id) throws UserDoesNotExistException{
		return ResponseEntity.status(HttpStatus.OK).body(new UserDto(userService.getUserById(id)));
	}

	@PutMapping
	public ResponseEntity<UserDto> handleEditUser(@AuthenticationPrincipal User principal, @RequestBody EditUserDto editedUser) throws UserDoesNotExistException, InvalidCredentialsException{
		return ResponseEntity.status(HttpStatus.OK).body(new UserDto(userService.editUser(principal, editedUser)));
	}
	
	@GetMapping("/get-user")
	public ResponseEntity<UserDto> handleGetUser(@AuthenticationPrincipal User user){
		return ResponseEntity.status(HttpStatus.OK).body(new UserDto(user));
	}
	
	@PostMapping("/new-admin")
	public ResponseEntity<UserDto> handleAddNewAdmin(@RequestBody RegisterRequest request) throws InvalidCredentialsException{
		return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(userService.addNewAdmin(request)));
	}
}
