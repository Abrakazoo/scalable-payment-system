package com.acme.payments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class User {
	
	@Id
	private UUID id;

	public User() {}
	
	User(String id) {
		this.id = UUID.fromString(id);
	}
	
	public UUID getId() {
		return this.id;
	}
}
