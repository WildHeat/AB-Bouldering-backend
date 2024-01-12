package com.abb.abbouldering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEvent;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

@RestController
@RequestMapping("/api/v1/stripe")
public class StripeController {
	
	@Autowired 
	private StripeService stripeService;
	
	@GetMapping("/all/{id}")
	public ResponseEntity<String> addUserToEvent(@AuthenticationPrincipal User user, @PathVariable long id) throws StripeException, EventDoesNotExistException{
		return ResponseEntity.ok(stripeService.handleCreateCheckoutSession(user, id));		
	}
	
	@PostMapping("/event")
	public ResponseEntity handleNewEventNotification(@RequestBody Event stripeEvent) throws UserDoesNotExistException, EventDoesNotExistException, UserIsAlreadySignedUpForEvent {
//		System.out.println(stripeEvent);
		stripeService.handleStripeEvent(stripeEvent);
		return ResponseEntity.ok().build();
	}
}
