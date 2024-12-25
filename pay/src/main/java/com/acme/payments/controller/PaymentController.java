package com.acme.payments.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.acme.payments.exception.CreatePaymentErrorException;
import com.acme.payments.exception.PaymentNotFoundException;
import com.acme.payments.model.Payer;
import com.acme.payments.model.Payment;
import com.acme.payments.model.PaymentMethod;
import com.acme.payments.model.User;
import com.acme.payments.repository.PaymentRepository;
import com.acme.payments.service.ProducerService;
import com.acme.payments.repository.PayerRepository;
import com.acme.payments.controller.assembler.PaymentModelAssembler;

@RestController
public class PaymentController {
	
	private final PaymentRepository paymentRepository;
	private final PayerRepository payerRepository;
	private final PaymentModelAssembler assembler;
	private final ProducerService producerService;
	
	PaymentController(PaymentRepository repository,
			          PayerRepository payerRepository,
			          PaymentModelAssembler assembler,
			          ProducerService producerService) {
		this.paymentRepository = repository;
		this.payerRepository = payerRepository;
		this.assembler = assembler;
		this.producerService = producerService;
	}
    
	/**
	 * TODO `PaymentController` should usually have a `findAll` method 
	 * which is:
	 * 1. Outside the scope of this demo
	 * 2. A "terrible anti-pattern" for big data tables, without e.g. pagination.
	 * see: https://vladmihalcea.com/spring-data-findall-anti-pattern/
	/** 
	 
	 * Get payment by id - used for hypermedia links in method responses
	 * example: http://localhost:8081/payments/12274a47-b6c6-41bf-81af-116416653306
	 * @param id The payment id
	 * @return The payment entity model or 404 Not Found
	 * @throws PaymentNotFoundException if payment is not found
	 */
    @GetMapping("/payments/{id}")
    public EntityModel<Payment> getPayment(@PathVariable UUID id) {
    	Payment payment = paymentRepository.findById(id)
    			.orElseThrow(() -> new PaymentNotFoundException(id));
        
    	return assembler.toModel(payment); 
    }
    
    /**
     * ENDPOINT #1. Create payment
     * example:
	 * curl --location --request POST 'localhost:8081/payments' \
		--data-raw '{
			"payment_id": "12274a47-b6c6-41bf-81af-116416653307",
			"amount": 70.5,
			"currency": "USD",
			"payerId": "e8af92bd-1910-421e-8de0-cb3dcf9bf44E",
			"payeeId": "4c3e304e-ce79-4f53-bb26-4e198e6c780B",
			"orderId": "c1c3ed5e-f500-444c-9207-5a0d532e9fe9",
			"paymentMethodId": "8e28af1b-a3a0-43a9-96cc-57d66dd68294",
			"payer_country": "US"
		}'
		
	 * @param newPayment payment payload
     * @return Http response entity of result - 201 Created 
     */
    @PostMapping("/payments")
    ResponseEntity<?> createPayment(@RequestBody Payment newPayment) { 
    	if (paymentRepository.existsById(newPayment.getId())) {
			throw new CreatePaymentErrorException(newPayment.getId().toString());
		}

		Payment payment = paymentRepository.save(newPayment);
    	EntityModel<Payment> paymentModel = assembler.toModel(payment);
    	// Save payment to auxiliary "Payer" table for better retrieval by payer id
    	payerRepository.save(new Payer(newPayment.getPayer().getId(),
    								   newPayment.getId()));
    	// Publish payment to Kafka queue
    	this.producerService.publishToKafka(payment);
    	return ResponseEntity.created(paymentModel.getRequiredLink(IanaLinkRelations.SELF)
    			.toUri()).body(paymentModel);
    }
    
    /**
     * ENDPOINT #2. Get payment methods by payer id
     * example: 
	 * http://localhost:8081/payments/payment_methods?payer_id=e8af92bd-1910-421e-8de0-cb3dcf9bf44d
	 * 
	 * @param payerId the UUID of the payer as a String
     * @return Payment methods associated with payer as a collection model
	 * @throws 
    */
    @GetMapping("payments/payment_methods")
    @ResponseBody
    CollectionModel<EntityModel<PaymentMethod>> getPaymentMethodsByPayerId(
    		@RequestParam(name="payer_id") String payerId) {
    	List<UUID> payerIdList = new ArrayList<UUID>();
    	payerIdList.add(UUID.fromString(payerId));
    	List<EntityModel<PaymentMethod>> paymentMethodModels = 
			new ArrayList<EntityModel<PaymentMethod>>();
    	Iterable<Payer> payerPayments = payerRepository.findAllById(payerIdList);
    	for(Payer payer : payerPayments) {
    		UUID id = payer.getPaymentId();
    		Payment payment = paymentRepository.findById(id)
    				.orElseThrow(() -> new PaymentNotFoundException(id));	
    		PaymentMethod paymentMethod = payment.getPaymentMethod();
    		paymentMethodModels.add(EntityModel.of(paymentMethod,
    				linkTo(methodOn(PaymentMethodControllerRest.class)
    						.getPaymentMethod(paymentMethod.getId())).withSelfRel(),
        	        linkTo(methodOn(PaymentMethodControllerRest.class)
        	        		.all()).withRel("payments")));
    	}
    	return CollectionModel.of(paymentMethodModels, 
    			linkTo(methodOn(PaymentMethodControllerRest.class).all()).withSelfRel());
    }
    
    /**
     * ENDPOINT #3. Get payees by payer id
	 * example:
	 * http://localhost:8081/payments/payees?payer_id=e8af92bd-1910-421e-8de0-cb3dcf9bf44d
     * @param payerId The UUID of the payer as a String
     * @return Payeed associated with the payer as a collection model of User entities
     */
    @GetMapping("payments/payees")
    @ResponseBody
    CollectionModel<EntityModel<User>> getPayeesByPayerId(@RequestParam(name="payer_id") String payerId) {
    	List<UUID> payerIdList = new ArrayList<UUID>();
    	List<EntityModel<User>> payeeModels = new ArrayList<EntityModel<User>>();
    	payerIdList.add(UUID.fromString(payerId));
    	Iterable<Payer> payerPayments = payerRepository.findAllById(payerIdList);
    	for(Payer payer : payerPayments) {
    		UUID id = payer.getPaymentId();
    		Payment payment = paymentRepository.findById(id)
    				.orElseThrow(() -> new PaymentNotFoundException(id));
    		User payee = payment.getPayee();
    		payeeModels.add(EntityModel.of(payee,
    				linkTo(methodOn(UserControllerRest.class).getUser(payee.getId())).withSelfRel(),
        	        linkTo(methodOn(UserControllerRest.class).all()).withRel("users")));
    	}
    	return CollectionModel.of(payeeModels, linkTo(methodOn(UserControllerRest.class).all()).withSelfRel());
    }
    

}
