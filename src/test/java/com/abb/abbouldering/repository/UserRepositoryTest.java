package com.abb.abbouldering.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepo;

	@Test
	public void testUserRepository_Save_returnsSavedUser() {
		User user = new UserBuilder()
				.email("test@email.com")
				.password("Password123")
				.firstName("test")
				.lastName("lastname")
				.build();

		User savedUser = userRepo.save(user);
		
		assertTrue(savedUser.equals(user));
	}
	
	@Test
	public void testUserRepository_findByEmailIgnoreCase_returnsSavedUserWhileIgnoringCase() {
		User user = new UserBuilder()
				.email("test@email.com")
				.password("Password123")
				.firstName("test")
				.lastName("lastname")
				.build();

		userRepo.save(user);
		User savedUser = userRepo.findByEmailIgnoreCase(user.getEmail().toUpperCase()).get();
		
		assertEquals(user, savedUser);
	}

	@Test
	public void testUserRepository_findByRole_returnsAllUsersFromAdminRole() {
		User adminUser = new UserBuilder()
				.email("test@email.com")
				.password("Password123")
				.firstName("test")
				.lastName("lastname")
				.role(Role.ADMIN)
				.build();
		
		userRepo.save(adminUser);
		
		ArrayList<User> listOfAdminUsers = userRepo.findByRole(Role.ADMIN);
		
		assertTrue(listOfAdminUsers.contains(adminUser));
	}
	
	@Test
	public void testUserRepository_deleteById_removesUser() {
		User user = new UserBuilder()
				.email("test@email.com")
				.password("Password123")
				.firstName("test")
				.lastName("lastname")
				.build();

		userRepo.save(user);
		
		assertTrue(userRepo.findById(user.getId()).isPresent());
		userRepo.deleteById(user.getId());
		assertFalse(userRepo.findById(user.getId()).isPresent());
	}
	
	@Test
	public void testUserRepository_save_updatesExistingUser() {
		String originalEmail = "test@email.com";
		User user = new UserBuilder()
				.email(originalEmail)
				.password("Password123")
				.firstName("test")
				.lastName("lastname")
				.build();

		User savedUser = userRepo.save(user);
		
		savedUser.setEmail("changingEmail@email.com");
		User updatedUser = userRepo.save(user);
	
		assertFalse(updatedUser.getEmail().equals(originalEmail));	
		
	}
	
	


}
