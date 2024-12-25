package com.acme.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CreatePaymentErrorAdvice {
	
	@ExceptionHandler(CreatePaymentErrorException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	String createPaymentErrorHandler(CreatePaymentErrorException ex) {
		return ex.getMessage();
	}
}
