package com.acme.payments.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.acme.payments.model.User;

public interface UserRepository extends CrudRepository<User, UUID> {

}
