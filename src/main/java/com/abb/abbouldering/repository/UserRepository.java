package com.abb.abbouldering.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abb.abbouldering.model.Role;
import com.abb.abbouldering.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmailIgnoreCase(String email);
	ArrayList<User> findByRole(Role role);
}
