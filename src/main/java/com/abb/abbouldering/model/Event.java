package com.abb.abbouldering.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_ID_GEN")
	@SequenceGenerator(name = "EVENT_ID_GEN", sequenceName = "event_id_seq", allocationSize = 1, initialValue = 10)
	private long id;
	@NotBlank
	private String title;
	private String smallDescription;
	@NotBlank
	private String description;
	@Min(0)
	private double price;
	@Min(1)
	@Max(100)
	private int maxSize;
	private LocalDateTime date;
	@ManyToOne(optional = false)
	private User organiser;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinTable(name = "event_users_table", joinColumns = {
			@JoinColumn(name = "event_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id") })
	private List<User> users;
	private String imageUrl;

	public Event() {
	}

	public Event(@NotBlank String title, String smallDescription, @NotBlank String description, @Min(0) double price,
			@Min(1) @Max(100) int maxSize, LocalDateTime date, User organiser, String imageUrl) {
		super();
		this.title = title;
		this.smallDescription = smallDescription;
		this.description = description;
		this.price = price;
		this.maxSize = maxSize;
		this.date = date;
		this.organiser = organiser;
		this.imageUrl = imageUrl;
		this.users = new ArrayList<User>();
	}

	public Event(long id, @NotBlank String title, String smallDescription, @NotBlank String description,
			@Min(0) double price, @Min(1) @Max(100) int maxSize, LocalDateTime date, User organiser, List<User> users,
			String imageUrl) {
		super();
		this.id = id;
		this.title = title;
		this.smallDescription = smallDescription;
		this.description = description;
		this.price = price;
		this.maxSize = maxSize;
		this.date = date;
		this.organiser = organiser;
		this.users = users;
		this.imageUrl = imageUrl;
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

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<User> getClimbers() {
		return users;
	}

	public void setClimbers(List<User> users) {
		this.users = users;
	}

	public User getOrganiser() {
		return organiser;
	}

	public void setOrganiser(User organiser) {
		this.organiser = organiser;
	}

	public void addUserToEvent(User user) {
		this.users.add(user);
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
