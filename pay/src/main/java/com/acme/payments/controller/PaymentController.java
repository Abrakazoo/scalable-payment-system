package com.acme.payments.controller;

import com.acme.payments.controller.assembler.PaymentMethodAssembler;
import com.acme.payments.controller.assembler.PaymentModelAssembler;
import com.acme.payments.controller.assembler.UserModelAssembler;
import com.acme.payments.exception.PaymentNotFoundException;
import com.acme.payments.model.Payment;
import com.acme.payments.service.PaymentService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
	private final PaymentModelAssembler paymentModelAssembler;
	private final PaymentMethodAssembler paymentMethodAssembler;
	private final UserModelAssembler userModelAssembler;

	PaymentController(PaymentService paymentService,
					  PaymentModelAssembler paymentModelAssembler,
					  PaymentMethodAssembler paymentMethodAssembler,
					  UserModelAssembler userModelAssembler) {
        this.paymentService = paymentService;
        this.paymentModelAssembler = paymentModelAssembler;
		this.paymentMethodAssembler = paymentMethodAssembler;
        this.userModelAssembler = userModelAssembler;
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
	@PostMapping("/")
	@Transactional
	ResponseEntity<?> createPayment(@RequestBody Payment newPayment) {
		var payment = paymentService.createPayment(newPayment);
		EntityModel<Payment> paymentModel = paymentModelAssembler.toModel(payment);
		return ResponseEntity.created(paymentModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(paymentModel);
	}

	/**
	 * Get payment by id - used for hypermedia links in method responses
	 * example: http://localhost:8081/payments/12z274a47-b6c6-41bf-81af-116416653306
	 * @param id The payment id
	 * @return The payment entity model or 404 Not Found
	 * @throws PaymentNotFoundException if payment is not found
	 */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(paymentModelAssembler.toModel(
				paymentService.findById(id)
		));
    }

    /**
     * ENDPOINT #2. Get payment methods by payer id
     * example: 
	 * http://localhost:8081/payments/payment_methods?payer_id=e8af92bd-1910-421e-8de0-cb3dcf9bf44d
	 * 
	 * @param payerId the UUID of the payer as a String
     * @return Payment methods associated with payer as a collection model
     */
    @GetMapping("/methods")
	public ResponseEntity<?> findPaymentMethodsByPayerId(@RequestParam(name="payer_id") String payerId) {
		return ResponseEntity.ok(
				paymentMethodAssembler.toCollectionModel(
						paymentService.findPaymentMethodsByPayerId(payerId)
				)
		);
    }
    
    /**
     * ENDPOINT #3. Get payees by payer id
	 * example:
	 * http://localhost:8081/payments/payees?payer_id=e8af92bd-1910-421e-8de0-cb3dcf9bf44d
     * @param payerId The UUID of the payer as a String
     * @return Payees associated with the payer as a collection model of User entities
     */
    @GetMapping("/payees")
    public ResponseEntity<?> findPayeesByPayerId(@RequestParam(name="payer_id") String payerId) {
		return ResponseEntity.ok(
				userModelAssembler.toCollectionModel(
						paymentService.findPayeesByPayerId(payerId)
				)
		);
    }
}
