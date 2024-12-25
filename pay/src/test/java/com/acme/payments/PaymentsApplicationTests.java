package com.acme.payments;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.acme.payments.controller.PaymentController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PaymentsApplicationTests {

	@Autowired
	private PaymentController paymentController;
	
	@Test
	void contextLoads() throws Exception{
		assertThat(paymentController).isNotNull();
	}

}
