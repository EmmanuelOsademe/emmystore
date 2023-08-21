package com.emmydev.ecommerce.client.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CategoryValidator implements ConstraintValidator<ValidCategory, String> {

    private static final Set<String> categories = new HashSet<>(Arrays.asList("electronics", "fashion", "kitchen", "computing", "home", "office", "general"));

    @Override
    public void initialize(ValidCategory constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
        return categories.contains(category);
    }
}
