package com.acme.payments.controller.assembler;

import com.acme.payments.model.PaymentMethod;
import com.acme.payments.service.PaymentMethodService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentMethodAssembler implements RepresentationModelAssembler<PaymentMethod, EntityModel<PaymentMethod>> {

    @NonNull
    @Override
    public EntityModel<PaymentMethod> toModel(@NonNull PaymentMethod entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PaymentMethodService.class).findById(entity.getId())).withSelfRel(),
                linkTo(methodOn(PaymentMethodService.class).findAll()).withRel("payments")
        );
    }
}
