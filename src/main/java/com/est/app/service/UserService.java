package com.est.app.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.est.app.entity.User;


public interface UserService extends UserDetailsService {
    Optional<User> getUserByUsername(String username);
    
    User create(User user);
    
}

