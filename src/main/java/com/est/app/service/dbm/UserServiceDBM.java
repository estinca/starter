package com.est.app.service.dbm;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.est.app.dao.UserDAO;
import com.est.app.entity.User;
import com.est.app.service.UserService;


@Service
@Transactional
public class UserServiceDBM implements UserService {
	

	private final UserDAO userDAO;
	
	@Autowired
	public UserServiceDBM( UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 return getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " username not found"));
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		return userDAO.findByUsername(username);
	}

	@Override
	public User create(User user) {
		return userDAO.saveAndFlush(user);
	}

}
