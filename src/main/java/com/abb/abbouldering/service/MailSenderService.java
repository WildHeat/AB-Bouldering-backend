package com.abb.abbouldering.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.InvalidEmailException;

@Service
public class MailSenderService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String toEmail, String subject, String body, String name) throws InvalidCredentialsException, InvalidEmailException {
		
		if(toEmail == null || toEmail == "") {
			throw new InvalidCredentialsException("Invalid email");
		}
		
		if(subject == null || subject == "") {
			throw new InvalidEmailException("Request needs a subject");
		}
		
		if(body == null || body == "") {
			throw new InvalidEmailException("Request needs a body");
		}
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("lowid11@googlemail.com");
		message.setTo(toEmail);
		message.setText(body + "\nWe will get back to you as soon as possible! Thank you "+ name);
		message.setSubject(subject);
		mailSender.send(message);
		System.out.println("Mail sent");
	}
}
