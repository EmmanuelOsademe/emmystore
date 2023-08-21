package com.emmydev.ecommerce.client.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ManufacturerValidator implements ConstraintValidator<ValidManufacturer, String> {

    private static final Set<String> manufacturers = new HashSet<>(Arrays.asList("lidl", "marcos", "argos", "adidas", "nike"));

    @Override
    public void initialize(ValidManufacturer constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String manufacturer, ConstraintValidatorContext constraintValidatorContext) {
        return manufacturers.contains(manufacturer);
    }
}
