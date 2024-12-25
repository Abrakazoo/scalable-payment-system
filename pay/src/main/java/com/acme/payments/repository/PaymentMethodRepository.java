package com.acme.payments.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.acme.payments.model.PaymentMethod;

public interface PaymentMethodRepository extends CrudRepository<PaymentMethod, UUID> {

}
