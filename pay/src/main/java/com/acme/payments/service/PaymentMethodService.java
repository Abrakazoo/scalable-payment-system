package com.acme.payments.service;

import com.acme.payments.exception.PaymentMethodNotFoundException;
import com.acme.payments.model.PaymentMethod;
import com.acme.payments.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    public PaymentMethodService(PaymentMethodRepository repository) {
        this.repository = repository;
    }

    public Iterable<PaymentMethod> findAll() {
        return repository.findAll();
    }

    public PaymentMethod findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new PaymentMethodNotFoundException(id));
    }
}