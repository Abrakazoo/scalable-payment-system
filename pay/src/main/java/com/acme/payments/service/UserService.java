package com.acme.payments.service;

import com.acme.payments.exception.UserNotFoundException;
import com.acme.payments.model.User;
import com.acme.payments.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public User findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}