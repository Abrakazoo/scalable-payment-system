package com.acme.payments.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class PaymentMethod {
	@Id
	private UUID id;
	
	public PaymentMethod() {}
	
	public PaymentMethod(String paymentMethodId) {
		this.id = UUID.fromString(paymentMethodId);
	}
	
	public PaymentMethod(UUID paymentMethodId) {
		this.id = paymentMethodId;
	}
	
	@OneToMany(mappedBy = "paymentMethod")
	private List<Payment> payments;
	
	public UUID getId() {
		return this.id;
	}
	
}
