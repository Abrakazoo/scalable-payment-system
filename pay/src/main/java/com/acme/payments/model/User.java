package com.acme.payments.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
	
	@Id
	private UUID id;

	User() {}
	
	User(String id) {
		this.id = UUID.fromString(id);
	}
	
	public UUID getId() {
		return this.id;
	}
}
