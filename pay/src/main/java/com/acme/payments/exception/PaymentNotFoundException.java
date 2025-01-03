package com.acme.payments.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
	
	// required for serialization
	private static final long serialVersionUID = 1L;
	
	public PaymentNotFoundException(UUID id) {
		super("Could not find payment " + id);
	}

}
