package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import javax.swing.text.html.Option;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> found = userRepository.findById(id);
		if(found.isPresent())
		{
			this.logger.info("Found user with id {}",id);
			return ResponseEntity.ok(found.get());
		}
		this.logger.error("User with id {} not found",id);
		return ResponseEntity.of(found);//.notFound();
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null)
		{
			this.logger.error("User with username {} not found",username);
			return ResponseEntity.notFound().build();
		}
		this.logger.info("Found user with name {}",username);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		try {

			user.setUsername(createUserRequest.getUsername());
			logger.info("User name is set to {}", createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			if (createUserRequest.getPassword().length() < 7 ||
					!createUserRequest.getConfirmPassword().equals(createUserRequest.getPassword())) {
				this.logger.error("Error with user password. Cannot create user {} " +
								"password length must be > 7 and both passwords must be the same",
						createUserRequest.getUsername());
				return ResponseEntity.badRequest().body(user);
			}
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
			userRepository.save(user);
			this.logger.info("User {} created successfully...", createUserRequest.getUsername());
			return ResponseEntity.ok(user);
		}catch (Exception ex)
		{
			this.logger.error(ex.getMessage());
			return ResponseEntity.badRequest().body(user);
		}

	}
	
}
