package com.acme.payments.init;

import com.acme.payments.model.Payer;
import com.acme.payments.model.Payment;
import com.acme.payments.repository.PayerRepository;
import com.acme.payments.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class LoadDB {
	
	private static final String PAYMENT_ONE_ID = "12274a47-b6c6-41bf-81af-116416653306";
	private static final String PAYMENT_TWO_ID = "12274a47-b6c6-41bf-81af-116416654495";
	private static final Float PAYMENT_ONE_AMOUNT = 70.5f;
	private static final Float PAYMENT_TWO_AMOUNT = 170.5f;
	private static final String USD = "USD";
	private static final String US = "US";
	private static final String PAYER_ONE_ID = "e8af92bd-1910-421e-8de0-cb3dcf9bf44d";
	private static final String PAYEE_ONE_ID = "4c3e304e-ce79-4f53-bb26-4e198e6c780a";
	private static final String PAYMENT_METHOD_ONE_ID = "8e28af1b-a3a0-43a9-96cc-57d66dd68294";
	private static final String ORDER_ONE_ID = "c1c3ed5e-f500-444c-9207-5a0d532e9fe9";
	private static final String ORDER_TWO_ID = "c1c3ed5e-f500-444c-9207-5a0d532e9fe0";
	
	private static final Logger log = LoggerFactory.getLogger(LoadDB.class);
	
	@Bean
	CommandLineRunner initDatabase(PaymentRepository paymentRepository,
								   PayerRepository payerRepository) {

	    return args -> {
            log.info("Preloading payment one: {}", paymentRepository.save(
                    new Payment(PAYMENT_ONE_ID,
                            PAYMENT_ONE_AMOUNT,
                            USD,
                            US,
                            PAYER_ONE_ID,
                            PAYEE_ONE_ID,
                            PAYMENT_METHOD_ONE_ID,
                            ORDER_ONE_ID)));
	    	// Save payment id to auxiliary "Payer" table
            log.info("Preloading payer one: {}", payerRepository.save(
                    new Payer(UUID.fromString(PAYER_ONE_ID), UUID.fromString(PAYMENT_ONE_ID))));
            log.info("Preloading payment two: {}", paymentRepository.save(
                    new Payment(PAYMENT_TWO_ID,
                            PAYMENT_TWO_AMOUNT,
                            USD,
                            US,
                            PAYER_ONE_ID,
                            PAYEE_ONE_ID,
                            PAYMENT_METHOD_ONE_ID,
                            ORDER_TWO_ID)));
	    	// Save payment id to auxiliary "Payer" table
            log.info("Preloading payer two: {}", payerRepository.save(
                    new Payer(UUID.fromString(PAYER_ONE_ID), UUID.fromString(PAYMENT_ONE_ID))));
	    };
	  }
}
