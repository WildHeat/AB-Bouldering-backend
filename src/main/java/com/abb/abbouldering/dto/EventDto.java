package com.abb.abbouldering.dto;

import java.time.LocalDate;
import java.util.ArrayList;

import com.abb.abbouldering.model.Event;
import com.abb.abbouldering.model.User;

public class EventDto {

	private long id;
	private String title;
	private String smallDescription;
	private String description;
	private double price;
	private int maxSize;
	private LocalDate date;
	private String organiser;
	private int spacesLeft;

	public EventDto() {
	}

	public EventDto(Event event) {
		this.id = event.getId();
		this.title = event.getTitle();
		this.smallDescription = event.getSmallDescription();
		this.description = event.getDescription();
		this.price = event.getPrice();
		this.maxSize = event.getMaxSize();
		this.date = event.getDate();
		this.organiser = event.getOrganiser().getFirstName() + event.getOrganiser().getLastName();
		this.spacesLeft = maxSize - event.getClimbers().size();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSmallDescription() {
		return smallDescription;
	}

	public void setSmallDescription(String smallDescription) {
		this.smallDescription = smallDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getSpacesLeft() {
		return spacesLeft;
	}

	public void setSpacesLeft(int spacesLeft) {
		this.spacesLeft = spacesLeft;
	}

	public String getOrganiser() {
		return organiser;
	}

	public void setOrganiser(String organiser) {
		this.organiser = organiser;
	}

}
