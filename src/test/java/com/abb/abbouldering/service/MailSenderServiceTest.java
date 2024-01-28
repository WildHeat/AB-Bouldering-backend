package com.abb.abbouldering.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.InvalidEmailException;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.model.UserBuilder;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

	@Mock
	private JavaMailSender mockMailSender;

	@InjectMocks
	private MailSenderService mailService;

	private User user;
	private Event event;

	@BeforeEach
	void init() {
		user = new UserBuilder().email("email@email.com").password("Password123").role(Role.ADMIN).firstName("first")
				.lastName("last").build();
		event = new Event("title", "smallDescription", "description", 23.0, 1, LocalDateTime.now(), user, "imageUrl");
	}

	@Test
	void testMailSenderService_isNotNull() {
		assertThat(mailService).isNotNull();
	}
	
	@Test
	void testMailSenderService_sendEmail_callsMailSenderSend() throws InvalidCredentialsException, InvalidEmailException {
		String toEmail = "email@email.com";
		String subject = "Subject";
		String body = "message body";
		String name = user.getFirstName();
		
		mailService.sendEmail(toEmail, subject, body, name);
		verify(mockMailSender).send(Mockito.any(SimpleMailMessage.class));
	}
	
	@Test
	void testMailSenderService_sendEmail_missingEmailThrowsInvalidCredentialsException() throws InvalidCredentialsException, InvalidEmailException {
		String toEmail = "";
		String subject = "Subject";
		String body = "message body";
		String name = user.getFirstName();
		assertThrows(InvalidCredentialsException.class, () -> mailService.sendEmail(toEmail, subject, body, name));
	}
	
	@Test
	void testMailSenderService_sendEmail_missingSubjectThrowsInvalidEmailException() throws InvalidCredentialsException, InvalidEmailException {
		String toEmail = "email@email.com";
		String subject = "";
		String body = "message body";
		String name = user.getFirstName();
		assertThrows(InvalidEmailException.class, () -> mailService.sendEmail(toEmail, subject, body, name));
	}
	
	@Test
	void testMailSenderService_sendEmail_missingbodyThrowsInvalidEmailException() throws InvalidCredentialsException, InvalidEmailException {
		String toEmail = "email@email.com";
		String subject = "Subject";
		String body = "";
		String name = user.getFirstName();
		assertThrows(InvalidEmailException.class, () -> mailService.sendEmail(toEmail, subject, body, name));
	}

	@Test
	void testMailSenderService_sendEmail_callsMailSenderSendWithMissingName() throws InvalidCredentialsException, InvalidEmailException {
		String toEmail = "email@email.com";
		String subject = "Subject";
		String body = "message body";
		String name = "";
		mailService.sendEmail(toEmail, subject, body, name);
		verify(mockMailSender).send(Mockito.any(SimpleMailMessage.class));
	}
	
	@Test
	void testMailSenderService_sendBookingConfirmationEmail_callsMailSenderSend() throws InvalidCredentialsException, InvalidEmailException {
		mailService.sendBookingConfirmationEmail(user, event);
		verify(mockMailSender).send(Mockito.any(SimpleMailMessage.class));
	}	

	@Test
	void testMailSenderService_sendBookingConfirmationEmail_throwsInvalidEmailExceptionWithNullReceiver() throws InvalidCredentialsException, InvalidEmailException {
		user = null;
		assertThrows(InvalidEmailException.class, () -> mailService.sendBookingConfirmationEmail(user, event));
	}	

	@Test
	void testMailSenderService_sendBookingConfirmationEmail_throwsInvalidEmailExceptionWithNullEvent() throws InvalidCredentialsException, InvalidEmailException {
		event = null;
		assertThrows(InvalidEmailException.class, () -> mailService.sendBookingConfirmationEmail(user, event));
	}	
}
