package com.abb.abbouldering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abb.abbouldering.service.StripeService;
import com.stripe.model.Event;

@RestController
@RequestMapping("/api/v1/stripe")
public class StripeController {
	
	@Autowired 
	private StripeService stripeService;
	
	@PostMapping
	public ResponseEntity<String> handleStripeEvent(@RequestBody Event stripeEvent){
		System.out.println(stripeEvent);
		stripeService.
		return ResponseEntity.ok(stripeEvent.toString());
		
	}
}
