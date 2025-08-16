package com.acme.payments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class PaymentOrder {

	@Id
	private UUID id;
	
	public PaymentOrder() {}
	
	PaymentOrder(String id) {
		this.id = UUID.fromString(id); 
	}

	public UUID getId() {
		return this.id;
	}
}
