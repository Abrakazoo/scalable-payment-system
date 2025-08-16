package com.acme.payments.exception;

public class CreatePaymentErrorException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public CreatePaymentErrorException(String id) {
		super("Cannot create payment, resource exists with same id " + id);
	}
}
