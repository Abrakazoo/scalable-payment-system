package com.acme.payments.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Payer {
	@Id
	@Column(name = "payer_id")
	private UUID payerId;
	private UUID paymentId;
	
	Payer() {}
	
	public Payer(UUID payerId, UUID paymentId) {
		this.payerId = payerId;
		this.paymentId = paymentId;
	}

	public UUID getPayerId() {
		return payerId;
	}
	public void setPayerId(UUID user) {
		this.payerId = user;
	}
	public UUID getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(UUID payment) {
		this.paymentId = payment;
	}
}
