package com.acme.payments.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.acme.payments.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
}
