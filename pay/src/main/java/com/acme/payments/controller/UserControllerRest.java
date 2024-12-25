package com.acme.payments.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.acme.payments.exception.UserNotFoundException;
import com.acme.payments.model.User;
import com.acme.payments.repository.UserRepository;

@RestController
public class UserControllerRest {
	
	private final UserRepository repository;
	
	UserControllerRest(UserRepository repository) {
		this.repository = repository;
	}
	
	// Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/users_rest")
    CollectionModel<EntityModel<User>> all() {
    	Iterable<User> users = repository.findAll();  
    	List<EntityModel<User>> userModels = new ArrayList<EntityModel<User>>();
    	for (User user : users) {
    		userModels.add(EntityModel.of(user,
    				linkTo(methodOn(UserControllerRest.class).getUser(user.getId())).withSelfRel(),
    				linkTo(methodOn(UserControllerRest.class).all()).withRel("users")));
    	}
    	
    	return CollectionModel.of(userModels, 
    			linkTo(methodOn(UserControllerRest.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]
    
    @GetMapping("/users_resr/{id}")
    EntityModel<User> getUser(@PathVariable UUID id) {
    	User user = repository.findById(id)
    			.orElseThrow(() -> new UserNotFoundException(id));
        return EntityModel.of(user,
        		linkTo(methodOn(UserControllerRest.class).getUser(id)).withSelfRel(),
    	        linkTo(methodOn(UserControllerRest.class).all()).withRel("users"));
    }
}