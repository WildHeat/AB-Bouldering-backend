package com.abb.abbouldering.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEvent;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.SessionWithUser;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.SessionWithUserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;

@Service
public class StripeService {

	@Autowired
	private SessionWithUserRepository sessionRepo;

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private EventService eventSerivce;
	
	@Value("${stripe.secret}")
	private String stripeSecret;

	public String handleCreateCheckoutSession(User user, long eventId) throws StripeException, EventDoesNotExistException {
		
		Optional<Event> optionalEvent = eventRepo.findById(eventId);
		
		if(optionalEvent.isEmpty()) throw new EventDoesNotExistException();
		
		Event event = optionalEvent.get();
		
		Stripe.apiKey = stripeSecret;

		SessionCreateParams params = SessionCreateParams.builder()
				.setCancelUrl("https://facebook.com")
				.setSuccessUrl("https://google.com")
				.addLineItem(SessionCreateParams.LineItem.builder().setPriceData(PriceData.builder().setCurrency("gbp")
						.setUnitAmount(Math.round(event.getPrice()*100))
						.setProductData(com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData
								.builder().setName(event.getTitle()).build())
						.build()).setQuantity(1L).build())
				.setMode(SessionCreateParams.Mode.PAYMENT).build();
		Session session = Session.create(params);
		sessionRepo.save(new SessionWithUser(session.getId(), user, event));
		return session.getUrl();
	}

	public void handleStripeEvent(com.stripe.model.Event stripeEvent) throws UserDoesNotExistException, EventDoesNotExistException, UserIsAlreadySignedUpForEvent {
		if(!"checkout.session.completed".equals(stripeEvent.getType())) {
			return;
		}
		
		Optional<StripeObject> optionalSession = stripeEvent.getDataObjectDeserializer().getObject();
		
		if(optionalSession.isEmpty()) {
			System.out.println("getDataObjectDerserializer is empty");
			throw new UserDoesNotExistException();
		}
		
		Session checkoutSession = (Session) optionalSession.get();
		
		Optional<SessionWithUser> optionalSessionData = sessionRepo.findById(checkoutSession.getId());
		if(optionalSessionData.isEmpty()) {
			System.out.println("cant find sessionWithUser with id " + checkoutSession.getId());
			throw new UserDoesNotExistException();
		}
		
		SessionWithUser sessionData = optionalSessionData.get();
		eventSerivce.addUserToEvent(sessionData.getUser(), sessionData.getEvent().getId());
		
	}

}
