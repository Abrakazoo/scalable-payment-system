package com.acme.payments.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
