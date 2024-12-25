package com.acme.payments.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
	
	// required for serialization
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException(UUID id) {
		super("Could not find user " + id);
	}

}
