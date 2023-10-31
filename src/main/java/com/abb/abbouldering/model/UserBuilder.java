package com.abb.abbouldering.model;

import jakarta.validation.constraints.NotBlank;

public class UserBuilder {

	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Role role;

	public UserBuilder() {
	}

	public UserBuilder firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public UserBuilder lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public UserBuilder email(String email) {
		this.email = email;
		return this;
	}

	public UserBuilder password(String password) {
		this.password = password;
		return this;
	}

	public UserBuilder role(Role role) {
		this.role = role;
		return this;
	}

	public User build() {
		return new User(this.firstName, this.lastName, this.email, this.password, this.role);
	}

}
