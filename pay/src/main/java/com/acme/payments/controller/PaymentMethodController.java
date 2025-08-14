package com.acme.payments.controller;

import com.acme.payments.exception.PaymentMethodNotFoundException;
import com.acme.payments.model.PaymentMethod;
import com.acme.payments.repository.PaymentMethodRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/payment_methods")
public class PaymentMethodController {
	
	private final PaymentMethodRepository repository;
	
	PaymentMethodController(PaymentMethodRepository repository) {
		this.repository = repository;
	}

    @GetMapping
    ResponseEntity<CollectionModel<EntityModel<PaymentMethod>>> all() {
    	Iterable<PaymentMethod> paymentMethods = repository.findAll();  
    	List<EntityModel<PaymentMethod>> paymentMethodModels = new ArrayList<>();
    	for (PaymentMethod paymentMethod : paymentMethods) {
    		paymentMethodModels.add(EntityModel.of(paymentMethod,
    				linkTo(methodOn(PaymentMethodController.class).getPaymentMethod(paymentMethod.getId())).withSelfRel(),
    				linkTo(methodOn(PaymentMethodController.class).all()).withRel("payment methods")));
    	}

		return ResponseEntity.ok(CollectionModel.of(paymentMethodModels,
				linkTo(methodOn(PaymentMethodController.class).all()).withSelfRel()));
    }
    
    @GetMapping("/{id}")
    ResponseEntity<EntityModel<PaymentMethod>> getPaymentMethod(@PathVariable UUID id) {
    	PaymentMethod paymentMethod = repository.findById(id)
    			.orElseThrow(() -> new PaymentMethodNotFoundException(id));
		return ResponseEntity.ok(EntityModel.of(paymentMethod,
				linkTo(methodOn(PaymentMethodController.class).getPaymentMethod(id)).withSelfRel(),
				linkTo(methodOn(PaymentMethodController.class).all()).withRel("payment methods")));
    }
}
