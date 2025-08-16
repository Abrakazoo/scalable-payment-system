package com.acme.payments.controller.assembler;

import com.acme.payments.controller.PaymentController;
import com.acme.payments.model.Payment;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<Payment, EntityModel<Payment>> {

	@NonNull
	@Override
	public EntityModel<Payment> toModel(@NonNull Payment entity) {
		return EntityModel.of(entity,
				linkTo(methodOn(PaymentController.class).findById(entity.getId())).withSelfRel()
		);
	}
}
