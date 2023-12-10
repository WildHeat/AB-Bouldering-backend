package com.abb.abbouldering.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.dto.EditUserDto;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserAlreadyExistsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.UserRepository;

@Service
public class UserService {

	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
	private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

	@Autowired
	private PasswordEncoder passwordEncoder;
	
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

	public User editUser(User principle, EditUserDto editUser) throws UserDoesNotExistException, InvalidCredentialsException {
		Optional<User> optionalUser = userRepo.findByEmailIgnoreCase(editUser.getEmail());

		if (optionalUser.isEmpty())
			throw new UserDoesNotExistException();
		
		User user = optionalUser.get();
		
		if (principle.getId() != user.getId())
			throw new InvalidCredentialsException("Wrong user");
				
		if(editUser.getPassword() != null && editUser.getPassword() != "") {
			newPasswordCheck(editUser,user);
		}

		if(editUser.getFirstName() == null || editUser.getFirstName().equals(""))
			throw new InvalidCredentialsException("Invalid credentials");
		if(editUser.getLastName() == null || editUser.getLastName().equals(""))
			throw new InvalidCredentialsException("Invalid credentials");
		
		user.setId(principle.getId());
		user.setFirstName(editUser.getFirstName());
		user.setLastName(editUser.getLastName());
		user.setRole(principle.getRole());
		
		return userRepo.save(user);
	}
	
	private void newPasswordCheck(EditUserDto editUser, User user) throws InvalidCredentialsException {
		if (!passwordEncoder.matches(editUser.getOldPassword(), user.getPassword()))
			throw new InvalidCredentialsException("Invalid credentials");
		if (!passwordPattern.matcher(editUser.getPassword()).matches()) {
			throw new InvalidCredentialsException("Password validation is not met");
		}
		user.setPassword(passwordEncoder.encode(editUser.getPassword()));
	}

	public User getUserById(long id) throws UserDoesNotExistException {
		Optional<User> optionalUser = userRepo.findById(id);
		if (optionalUser.isEmpty())
			throw new UserDoesNotExistException();
		return optionalUser.get();
	}

	public ArrayList<String> getAllAdminNames() {
		ArrayList<User> admins = userRepo.findByRole(Role.ADMIN);
		ArrayList<String> names = new ArrayList<String>();
		admins.forEach((user) -> {
			names.add(user.getId() + ":" + user.getFirstName() + " " + user.getLastName());
		});
		return names;
	}

}
