package com.emmydev.ecommerce.client.validation;

import com.emmydev.ecommerce.client.model.UserModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        UserModel userModel = (UserModel) obj;
        return userModel.getPassword().equals(userModel.getPasswordConfirmation());
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
