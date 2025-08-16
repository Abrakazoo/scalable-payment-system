package com.acme.payments.controller;

import com.acme.payments.controller.assembler.UserModelAssembler;
import com.acme.payments.model.User;
import com.acme.payments.service.UserService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

	public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }
	
	@GetMapping
    public ResponseEntity<?> findAll() {
    	var users = userService.findAll();
        return ResponseEntity.ok(userModelAssembler.toCollectionModel(users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> findById(@PathVariable UUID id) {
    	var user = userService.findById(id);
        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }
}