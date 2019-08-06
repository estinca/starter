package com.est.app.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.est.app.entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, String>{
	Optional<User> findByUsername(String username);
}

