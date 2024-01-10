package com.abb.abbouldering.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.SessionWithUserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;

@Service
public class StripeService {

	@Autowired
	private SessionWithUserRepository sessionRepo;
	
	@Value("${stripe.secret}")
	private String stripeSecret;

	public String handleCreateCheckoutSession() throws StripeException {
		Stripe.apiKey = stripeSecret;

		SessionCreateParams params = SessionCreateParams.builder().setCancelUrl("https://facebook.com")
				.setSuccessUrl("https://google.com")
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setPriceData(PriceData.builder().setCurrency("gbp").setUnitAmount(1000l).setProductData(com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData.builder().setName("EVNT").build()).build())
						.setQuantity(1L).build())
				.setMode(SessionCreateParams.Mode.PAYMENT).build();
		Session session = Session.create(params);	
		System.out.println(session);
		return session.getUrl();
	}
	
	// Use for the checkout events
	public void handleStripeEvent() {
		
	}
	
	public void handleStoringNewSession(User user, Long eventId) {
		
	}
}
