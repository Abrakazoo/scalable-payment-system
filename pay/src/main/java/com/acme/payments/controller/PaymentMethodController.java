package com.acme.payments.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.acme.payments.exception.PaymentMethodNotFoundException;
import com.acme.payments.model.PaymentMethod;
import com.acme.payments.repository.PaymentMethodRepository;

@RestController
public class PaymentMethodController {
	
	private final PaymentMethodRepository repository;
	
	PaymentMethodController(PaymentMethodRepository repository) {
		this.repository = repository;
	}
	
	// Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/payment_methods_rest")
    CollectionModel<EntityModel<PaymentMethod>> all() {
    	Iterable<PaymentMethod> paymentMethods = repository.findAll();  
    	List<EntityModel<PaymentMethod>> paymentMethodModels = new ArrayList<EntityModel<PaymentMethod>>();
    	for (PaymentMethod paymentMethod : paymentMethods) {
    		paymentMethodModels.add(EntityModel.of(paymentMethod,
    				linkTo(methodOn(PaymentMethodController.class).getPaymentMethod(paymentMethod.getId())).withSelfRel(),
    				linkTo(methodOn(PaymentMethodController.class).all()).withRel("payment methods")));
    	}
    	
    	return CollectionModel.of(paymentMethodModels, 
    			linkTo(methodOn(PaymentMethodController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]
    
    @GetMapping("/payment_methods_rest/{id}")
    EntityModel<PaymentMethod> getPaymentMethod(@PathVariable UUID id) {
    	PaymentMethod paymentMethod = repository.findById(id)
    			.orElseThrow(() -> new PaymentMethodNotFoundException(id));
        return EntityModel.of(paymentMethod,
        		linkTo(methodOn(PaymentMethodController.class).getPaymentMethod(id)).withSelfRel(),
    	        linkTo(methodOn(PaymentMethodController.class).all()).withRel("payment methods"));
    }
}
