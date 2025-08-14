package com.acme.payments.controller;

import com.acme.payments.exception.UserNotFoundException;
import com.acme.payments.model.User;
import com.acme.payments.repository.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	private final UserRepository repository;
	
	UserController(UserRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping
    ResponseEntity<CollectionModel<EntityModel<User>>> all() {
    	Iterable<User> users = repository.findAll();  
    	List<EntityModel<User>> userModels = new ArrayList<>();
    	for (User user : users) {
    		userModels.add(EntityModel.of(user,
    				linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel(),
    				linkTo(methodOn(UserController.class).all()).withRel("users")));
    	}

		return ResponseEntity.ok(CollectionModel.of(userModels,
				linkTo(methodOn(UserController.class).all()).withSelfRel()));
    }
    
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<User>> getUser(@PathVariable UUID id) {
    	User user = repository.findById(id)
    			.orElseThrow(() -> new UserNotFoundException(id));

		return ResponseEntity.ok(EntityModel.of(user,
				linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
				linkTo(methodOn(UserController.class).all()).withRel("users")));
    }
}