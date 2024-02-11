package com.abb.abbouldering.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.EventIsFullyBookedException;
import com.abb.abbouldering.exception.InvalidCredentialsException;
import com.abb.abbouldering.exception.InvalidEmailException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEventException;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.SessionWithUser;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.SessionWithUserRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;

@Service
public class StripeService {

	@Autowired
	private SessionWithUserRepository sessionRepo;

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private EventService eventService;

	@Value("${stripe.secret}")
	private String stripeSecret;

	public String handleCreateCheckoutSession(User user, long eventId) throws StripeException,
			EventDoesNotExistException, UserIsAlreadySignedUpForEventException, EventIsFullyBookedException, InvalidEmailException {
		Optional<Event> optionalEvent = eventRepo.findById(eventId);
		if (optionalEvent.isEmpty())
			throw new EventDoesNotExistException();

		Event event = optionalEvent.get();

		if (event.getPrice() <= 0) {
			eventService.addUserToEvent(user, event);
			return "";
		}

		if (eventService.isUserAlreadyInEvent(user, event))
			throw new UserIsAlreadySignedUpForEventException();

		if (event.getClimbers().size() >= event.getMaxSize()) {
			throw new EventIsFullyBookedException();
		}

		Stripe.apiKey = stripeSecret;

		SessionCreateParams params = SessionCreateParams.builder()
				.setSuccessUrl("https://abboulder.com/booking-completed")
				.setCancelUrl("https://abboulder.com/events/" + eventId)
				.addLineItem(SessionCreateParams.LineItem.builder().setPriceData(PriceData.builder().setCurrency("gbp")
						.setUnitAmount(Math.round(event.getPrice() * 100))
						.setProductData(com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData
								.builder().setName(event.getTitle()).build())
						.build()).setQuantity(1L).build())
				.setMode(SessionCreateParams.Mode.PAYMENT).build();
		Session session = Session.create(params);
		sessionRepo.save(new SessionWithUser(session.getId(), user, event));
		return session.getUrl();
	}

	public void handleStripeEvent(String requestBody, String stripeSig)
			throws InvalidCredentialsException, EventDoesNotExistException, UserIsAlreadySignedUpForEventException,
			EventIsFullyBookedException, InvalidEmailException {
		String endpointSecret = "whsec_Ci4rSOKfqi1cpxvDdPPEWJD2jkXzmDAY";
		com.stripe.model.Event event = null;

		try {
			event = Webhook.constructEvent(requestBody, stripeSig, endpointSecret);
		} catch (SignatureVerificationException e) {
			throw new InvalidCredentialsException("Did this come from stripe?");
		}

		if (!"checkout.session.completed".equals(event.getType())
				&& !"checkout.session.expired".equals(event.getType())) {
			return;
		}

		EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
		Session checkoutSession = null;
		if (dataObjectDeserializer.getObject().isPresent()) {
			checkoutSession = (Session) dataObjectDeserializer.getObject().get();
		} else {
			System.out.println("API MISSMATCH!");
		}

		Optional<SessionWithUser> optionalSessionData = sessionRepo.findById(checkoutSession.getId());
		if (optionalSessionData.isEmpty()) {
			System.out.println("cant find sessionWithUser with id " + checkoutSession.getId());
			return;
		}

		SessionWithUser sessionData = optionalSessionData.get();
		if ("checkout.session.completed".equals(event.getType())) {
			eventService.addUserToEvent(sessionData.getUser(), sessionData.getEvent());
		} else {
			sessionRepo.deleteById(sessionData.getId());
		}

	}

}
