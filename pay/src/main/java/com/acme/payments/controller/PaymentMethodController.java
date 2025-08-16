package com.acme.payments.controller;

import com.acme.payments.controller.assembler.PaymentMethodAssembler;
import com.acme.payments.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment_methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodAssembler paymentMethodAssembler;

	PaymentMethodController(PaymentMethodService paymentMethodService, PaymentMethodAssembler paymentMethodAssembler) {
        this.paymentMethodService = paymentMethodService;
        this.paymentMethodAssembler = paymentMethodAssembler;
    }

    @GetMapping
    ResponseEntity<?> findAll() {
    	var paymentMethods = paymentMethodService.findAll();
        return ResponseEntity.ok(paymentMethodAssembler.toCollectionModel(paymentMethods));
    }
    
    @GetMapping("/{id}")
    ResponseEntity<?> findById(@PathVariable UUID id) {
    	var paymentMethod = paymentMethodService.findById(id);
        return ResponseEntity.ok(paymentMethodAssembler.toModel(paymentMethod));
    }
}
