package com.acme.payments.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.acme.payments.model.Payer;

public interface PayerRepository extends CrudRepository<Payer, UUID> {

}
