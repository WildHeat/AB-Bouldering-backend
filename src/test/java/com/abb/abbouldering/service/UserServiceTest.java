package com.abb.abbouldering.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.abb.abbouldering.dto.EditUserDto;
import com.abb.abbouldering.dto.RegisterRequest;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;
import com.abb.abbouldering.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository mockUserRepo;

	@Mock
	private PasswordEncoder mockPasswordEncoder;

	@InjectMocks
	private UserService userSerivce;

	@Test
	void testUserService_deleteUser_validUserIDCallsUserRepoDeleteById() throws UserDoesNotExistException {
		User user = new UserBuilder().email("test@email.com").password("Password123").firstName("test")
				.lastName("lastname").build();

		when(mockUserRepo.existsById(Mockito.anyLong())).thenReturn(true);
		userSerivce.deleteUser(user.getId());
		verify(mockUserRepo).deleteById(user.getId());
	}

	@Test
	void testUserService_deleteUser_invalidUserIDThrowsUserDoesNotException() {
		when(mockUserRepo.existsById(Mockito.anyLong())).thenReturn(false);
		assertThrows(UserDoesNotExistException.class, () -> userSerivce.deleteUser(10l));
	}

	@Test
	void testUserService_getUserById_validUserIdReturnsUser() throws UserDoesNotExistException {
		User user = new UserBuilder().email("test@email.com").password("Password123").firstName("test")
				.lastName("lastname").build();

		when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
		User foundUser = userSerivce.getUserById(user.getId());
		assertThat(foundUser).isNotNull();
	}

	@Test
	void testUserService_getUserById_invalidUserIDThrowsUserDoesNotException() {
		when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		assertThrows(UserDoesNotExistException.class, () -> userSerivce.getUserById(10l));
	}

	@Test
	void testUserService_getAllAdminNames_callsUserRepoFindByRole() {

		User admin1 = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();

		User admin2 = new UserBuilder().email("email2@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first2").lastName("last2").build();

		User[] admins = { admin1, admin2 };
		ArrayList<User> adminList = new ArrayList(Arrays.asList(admins));

		when(mockUserRepo.findByRole(Role.ADMIN)).thenReturn(adminList);

		ArrayList<String> actualAdmins = userSerivce.getAllAdminNames();
		
		assertEquals(2, actualAdmins.size());
		assertEquals(admin1.getId() +":first last", actualAdmins.get(0));
		assertEquals(admin2.getId() +":first2 last2", actualAdmins.get(1));
		
	}

	@Test
	void testUserService_addNewAdmin_returnsUserWithValidCredentials() throws InvalidCredentialsException {
		RegisterRequest newAdmin = new RegisterRequest("testfirst", "testlast", "test@email.com", "Password123");
		when(mockPasswordEncoder.encode(Mockito.any())).thenReturn("encodedPassword123");
		userSerivce.addNewAdmin(newAdmin);
		verify(mockUserRepo).save(Mockito.any(User.class));
	}

	@Test
	void testUserService_addNewAdmin_throwsInvalidCredentialsExceptionWithInvalidEmail() {
		RegisterRequest newAdmin = new RegisterRequest("testfirst", "testlast", "testemail.com", "Password123");
		assertThrows(InvalidCredentialsException.class, () -> userSerivce.addNewAdmin(newAdmin));
	}

	@Test
	void testUserService_addNewAdmin_throwsInvalidCredentialsExceptionWithInvalidPassword() {
		RegisterRequest newAdmin = new RegisterRequest("testfirst", "testlast", "test@email.com", "invalidPassword");
		assertThrows(InvalidCredentialsException.class, () -> userSerivce.addNewAdmin(newAdmin));
	}

	@Test
	void testUserService_editUser_returnsUpdatedUser() throws UserDoesNotExistException, InvalidCredentialsException {

		User user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();

		EditUserDto editUser = new EditUserDto(user.getId(), "changeFirst", "ChangeSecond", user.getEmail(), null,
				user.getPassword());

		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(user));
		when(mockPasswordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

		userSerivce.editUser(user, editUser);
		verify(mockUserRepo).save(Mockito.any(User.class));
	}

	@Test
	void testUserService_editUser_WrongUserThrowsException()
			throws UserDoesNotExistException, InvalidCredentialsException {

		User user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();

		User user2 = new UserBuilder().email("email2@email.com").password("1Password123").role(Role.ADMIN)
				.firstName("1first").lastName("1last").build();

		EditUserDto editUser = new EditUserDto(user2.getId(), "changeFirst", "ChangeSecond", user.getEmail(), null,
				user.getPassword());

		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(user2));

		assertThrows(InvalidCredentialsException.class, () -> userSerivce.editUser(user2, editUser));
	}

	@Test
	void testUserService_editUser_emptyFirstNameThrowsException() throws UserDoesNotExistException, InvalidCredentialsException {

		User user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();

		EditUserDto editUser = new EditUserDto(user.getId(), "", "ChangeSecond", user.getEmail(), null,
				user.getPassword());

		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(user));
		when(mockPasswordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

		assertThrows(InvalidCredentialsException.class, () -> userSerivce.editUser(user, editUser));
	}

	@Test
	void testUserService_editUser_emptyLastNameThrowsException() throws UserDoesNotExistException, InvalidCredentialsException {
		
		User user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();
		
		EditUserDto editUser = new EditUserDto(user.getId(), "changeFirst", "", user.getEmail(), null,
				user.getPassword());
		
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(user));
		when(mockPasswordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		
		assertThrows(InvalidCredentialsException.class, () -> userSerivce.editUser(user, editUser));
	}

	@Test
	void testUserService_editUser_invalidNewPasswordThrowsException() throws UserDoesNotExistException, InvalidCredentialsException {
		
		User user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN)
				.firstName("first").lastName("last").build();
		
		EditUserDto editUser = new EditUserDto(user.getId(), "changeFirst", "", user.getEmail(), "password",
				user.getPassword());
		
		when(mockUserRepo.findByEmailIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(user));
		when(mockPasswordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		assertThrows(InvalidCredentialsException.class, () -> userSerivce.editUser(user, editUser));
	}
}
