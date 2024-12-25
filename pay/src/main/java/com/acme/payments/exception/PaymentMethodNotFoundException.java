package com.acme.payments.exception;

import java.util.UUID;

public class PaymentMethodNotFoundException extends RuntimeException {
	
	// required for serialization
	private static final long serialVersionUID = 1L;
	
	public PaymentMethodNotFoundException(UUID id) {
		super("Could not find payment method " + id);
	}
}
