package com.abb.abbouldering.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abb.abbouldering.dto.EventDto;
import com.abb.abbouldering.exception.EventAlreadyExistsException;
import com.abb.abbouldering.exception.EventDoesNotExistException;
import com.abb.abbouldering.exception.UserDoesNotExistException;
import com.abb.abbouldering.exception.UserIsAlreadySignedUpForEvent;
import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;
import com.abb.abbouldering.repository.EventRepository;
import com.abb.abbouldering.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private UserRepository userRepo;

	@Value("${stripe.secret}")
	private String stripeSecret;

	public String handleCreateSession() throws StripeException {
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

	public Event addEvent(EventDto eventDto) throws EventAlreadyExistsException, UserDoesNotExistException {
		if (eventRepo.existsById(eventDto.getId())) {
			throw new EventAlreadyExistsException();
		}
		Event event = new Event();
		event = convertEventDtoToEvent(event, eventDto);
		return eventRepo.save(event);
	}

	public void deleteEventById(long id) throws EventDoesNotExistException {
		if (!eventRepo.existsById(id))
			throw new EventDoesNotExistException();

		eventRepo.deleteById(id);
	}

	public Event updateEvent(EventDto eventDto) throws EventDoesNotExistException, UserDoesNotExistException {
		Optional<Event> optionalEvent = eventRepo.findById(eventDto.getId());
		if (optionalEvent.isEmpty())
			throw new EventDoesNotExistException();
		Event event = optionalEvent.get();
		event = convertEventDtoToEvent(event, eventDto);
		event = maxSizeCheck(event, eventDto);

		return eventRepo.save(event);
	}

	public EventDto getEventById(long id) throws EventDoesNotExistException {
		Optional<Event> optionalEvent = eventRepo.findById(id);

		if (optionalEvent.isEmpty())
			throw new EventDoesNotExistException();
		return new EventDto(optionalEvent.get());
	}

	public List<EventDto> getAllEvents() {
		ArrayList<EventDto> eventsDto = new ArrayList<EventDto>();
		for (Event event : eventRepo.findAll()) {
			eventsDto.add(new EventDto(event));
		}
		return eventsDto;
	}

	public EventDto addUserToEvent(User user, long id)
			throws EventDoesNotExistException, UserIsAlreadySignedUpForEvent {
		Optional<Event> optionalEvent = eventRepo.findById(id);
		if (optionalEvent.isEmpty()) {
			throw new EventDoesNotExistException();
		}
		Event event = optionalEvent.get();
		if (isUserAlreadyInEvent(user, event)) {
			throw new UserIsAlreadySignedUpForEvent();
		}
		event.addUserToEvent(user);
		return new EventDto(eventRepo.save(event));
	}

	private boolean isUserAlreadyInEvent(User user, Event event) {
		for (User tempUser : event.getClimbers()) {
			if (user.getId() == tempUser.getId()) {
				return true;
			}
		}
		return false;
	}

	private Event convertEventDtoToEvent(Event event, EventDto eventDto) throws UserDoesNotExistException {
		event.setDate(eventDto.getDate());
		event.setDescription(eventDto.getDescription());
		event.setImageUrl(eventDto.getImageUrl());
		event.setPrice(eventDto.getPrice());
		event.setSmallDescription(eventDto.getSmallDescription());
		event.setTitle(eventDto.getTitle());
		event.setMaxSize(eventDto.getMaxSize());

		long organiserId = Long.parseLong(eventDto.getOrganiser().split(":")[0]);
		Optional<User> optionalOrganiser = userRepo.findById(organiserId);
		if (optionalOrganiser.isEmpty())
			throw new UserDoesNotExistException();
		event.setOrganiser(optionalOrganiser.get());

		return event;
	}

	private Event maxSizeCheck(Event event, EventDto eventDto) {
		if (event.getClimbers() != null && event.getClimbers().size() > eventDto.getMaxSize()) {
			event.setMaxSize(event.getClimbers().size());
		} else {
			event.setMaxSize(eventDto.getMaxSize());
		}
		return event;
	}

	public List<EventDto> getAllEventsWhereUser(User user) {
		ArrayList<EventDto> eventsDto = new ArrayList<EventDto>();
		user.getEvents().forEach(event -> eventsDto.add(new EventDto(event)));
		return eventsDto;
	}

	public List<EventDto> getMyEvents(User user) {
		if (user.getRole() == Role.USER) {
			return getAllEventsWhereUser(user);
		}
		return getAllOrganisersEvents(user);
	}

	private List<EventDto> getAllOrganisersEvents(User user) {
		ArrayList<EventDto> eventsDto = new ArrayList<EventDto>();
		eventRepo.findByOrganiser(user).forEach(event -> eventsDto.add(new EventDto(event)));
		return eventsDto;
	}

	public List<EventDto> getTopEvents() {
		List<Event> events = eventRepo.findFirst4ByOrderByDateDesc();
		ArrayList<EventDto> eventsDto = new ArrayList<EventDto>();
		for (Event event : events) {
			eventsDto.add(new EventDto(event));
		}
		return eventsDto;
	}
}
