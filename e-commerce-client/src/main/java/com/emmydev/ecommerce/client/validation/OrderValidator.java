package com.emmydev.ecommerce.client.validation;

import com.emmydev.ecommerce.client.dto.OrderDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class OrderValidator implements ConstraintValidator<ValidOrder, Object> {
    @Override
    public void initialize(ValidOrder constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        OrderDto orderDto = (OrderDto) obj;

        return orderDto.getDeliveryOption().equalsIgnoreCase("home-delivery") && Objects.nonNull(orderDto.getAddress());
    }
}
