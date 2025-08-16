package com.acme.payments.controller.assembler;

import com.acme.payments.controller.UserController;
import com.acme.payments.model.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @NonNull
    @Override
    public EntityModel<User> toModel(@NonNull User entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(UserController.class).findById(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users")
        );
    }
}
