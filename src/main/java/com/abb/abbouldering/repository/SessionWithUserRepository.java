package com.abb.abbouldering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abb.abbouldering.model.SessionWithUser;

public interface SessionWithUserRepository extends JpaRepository<SessionWithUser, String>{

}
