package com.acme.payments.service;

import com.acme.payments.exception.CreatePaymentErrorException;
import com.acme.payments.exception.PaymentNotFoundException;
import com.acme.payments.model.Payer;
import com.acme.payments.model.Payment;
import com.acme.payments.model.PaymentMethod;
import com.acme.payments.model.User;
import com.acme.payments.repository.PayerRepository;
import com.acme.payments.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayerRepository payerRepository;
    private final ProducerService producerService;

    public PaymentService(PaymentRepository paymentRepository,
                          PayerRepository payerRepository,
                          ProducerService producerService) {
        this.paymentRepository = paymentRepository;
        this.payerRepository = payerRepository;
        this.producerService = producerService;
    }

    public Payment createPayment(Payment newPayment) {
        if (paymentRepository.existsById(newPayment.getId())) {
            throw new CreatePaymentErrorException(newPayment.getId().toString());
        }

        Payment payment = paymentRepository.save(newPayment);

        // TODO: should be removed all together with payer table
        payerRepository.save(new Payer(newPayment.getPayer().getId(), newPayment.getId()));

        this.producerService.publishToKafka(payment);

        return payment;
    }

    public Payment findById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public Set<PaymentMethod> findPaymentMethodsByPayerId(String payerId) {
        Set<PaymentMethod> paymentMethods = new HashSet<>();
        for(Payment payment : findPaymentsByPayerId(payerId)) {
            PaymentMethod paymentMethod = payment.getPaymentMethod();
            paymentMethods.add(paymentMethod);
        }

        return paymentMethods;
    }

    public Set<User> findPayeesByPayerId(String payerId) {
        Set<User> payees = new HashSet<>();
        for(Payment payment : findPaymentsByPayerId(payerId)) {
            User payee = payment.getPayee();
            payees.add(payee);
        }

        return payees;
    }

    private Set<Payment> findPaymentsByPayerId(String payerId) {
        var payerPayments = payerRepository.findAllById(List.of(UUID.fromString(payerId)));

        Set<Payment> payments = new HashSet<>();
        for(Payer payer : payerPayments) {
            UUID id = payer.getPaymentId();
            Payment payment = paymentRepository.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
            payments.add(payment);
        }

        return payments;
    }
}

