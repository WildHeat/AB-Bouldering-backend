package com.abb.abbouldering.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.UserAlreadyExists;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;

	public User addUser(User user) throws UserAlreadyExists {
		Optional<User> optionalUser = userRepo.findByEmailIgnoreCase(user.getEmail());
		
		if(optionalUser.isPresent()) {
			throw new UserAlreadyExists();
		}
		
		return userRepo.save(optionalUser.get());
		
	}
}
