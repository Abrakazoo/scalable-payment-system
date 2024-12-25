package com.acme.payments.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acme.payments.model.Payment;

@Service
public class ProducerService {
	
	private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

	@Autowired 
	KafkaTemplate<String, Payment> paymentKafkaTemplate;
	
	public ProducerService(KafkaTemplate<String, Payment> template) {
		this.paymentKafkaTemplate = template;
	}
	
	@Transactional
	public void publishToKafka(Payment payment) {
		CompletableFuture<SendResult<String, Payment>> future =
			this.paymentKafkaTemplate
				.send("payments", 0, payment.getId().toString(), payment);
			future.whenComplete((result, ex) -> {
				if (ex == null) {
					log.info("Successfully sent payment to kafka " + payment +
					"With result " + result);;
				}
				else {
					log.error("Exception raised while sending payment to kafka " +
						ex.getMessage());;
				}
		});
	}
}
