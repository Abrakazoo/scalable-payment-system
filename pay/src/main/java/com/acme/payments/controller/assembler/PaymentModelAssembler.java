package com.acme.payments.controller.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.acme.payments.controller.PaymentController;
import com.acme.payments.model.Payment;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<Payment, EntityModel<Payment>> {

	@Override
	public EntityModel<Payment> toModel(Payment entity) {
		return EntityModel.of(entity,
				linkTo(methodOn(PaymentController.class).getPayment(entity.getId())).withSelfRel());
	}
}
