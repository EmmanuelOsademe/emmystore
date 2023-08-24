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

        boolean validHomeDelivery = true;
        if(orderDto.getDeliveryOption().equalsIgnoreCase("home-delivery") &&
                (Objects.isNull(orderDto.getAddress()) || Objects.isNull(orderDto.getShippingFee()))
        ){
            validHomeDelivery = false;
        }

        boolean validPickup = true;
        if(orderDto.getDeliveryOption().equalsIgnoreCase("pick-up") &&
                Objects.nonNull(orderDto.getShippingFee())
        ){
            validPickup = false;
        }

        return validHomeDelivery && validPickup;
    }
}
