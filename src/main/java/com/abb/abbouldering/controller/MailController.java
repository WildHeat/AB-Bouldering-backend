package com.abb.abbouldering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.dto.ContactFormDto;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.InvalidEmailException;
import com.abb.abbouldering.service.MailSenderService;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
	
	@Autowired
	MailSenderService mailSenderService;
	
	@PostMapping
	public ResponseEntity<String> contactEmail (@RequestBody ContactFormDto mail) throws InvalidCredentialsException, InvalidEmailException{
		mailSenderService.sendEmail(mail.getEmail(), mail.getSubject(), mail.getMessage(), mail.getName());
		return ResponseEntity.ok("Message Sent");
		
	}
	
}
