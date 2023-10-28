package com.abb.abbouldering.service;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserAlreadyExistsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.UserRepository;

@Service
public class UserService {

	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
	private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

	@Autowired
	private UserRepository userRepo;

	public User addUser(User user) throws UserAlreadyExistsException, InvalidCredentialsException {
		Optional<User> optionalUser = userRepo.findByEmailIgnoreCase(user.getEmail());

		if (optionalUser.isPresent()) {
			throw new UserAlreadyExistsException();
		}

		if (!passwordPattern.matcher(user.getPassword()).matches()) {
			throw new InvalidCredentialsException("Password validation is not met");
		}

		return userRepo.save(user);
	}

	public void deleteUser(long id) throws UserDoesNotExistException {
		if (!userRepo.existsById(id)) {
			throw new UserDoesNotExistException();
		}
		userRepo.deleteById(id);
	}
	
	public User editUser(User user) throws UserDoesNotExistException, InvalidCredentialsException {
		Optional<User> optionalUser = userRepo.findById(user.getId());
		
		if(optionalUser.isEmpty()) {
			throw new UserDoesNotExistException();
		}
		
		if (!passwordPattern.matcher(user.getPassword()).matches()) {
			throw new InvalidCredentialsException("Password validation is not met");
		}
		
		return userRepo.save(user);
	}
	
	public User getUserById(long id) throws UserDoesNotExistException {
		Optional<User> optionalUser = userRepo.findById(id);
		if(optionalUser.isEmpty()) throw new UserDoesNotExistException();
		return optionalUser.get();
	}

}
