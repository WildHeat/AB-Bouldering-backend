package com.abb.abbouldering.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GEN")
	@SequenceGenerator(name = "USER_ID_GEN", sequenceName = "user_id_seq", allocationSize = 1, initialValue = 10)
	private long id;
	private String email;
	private String password;
	private String role;
}
