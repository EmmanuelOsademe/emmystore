package com.emmydev.ecommerce.client.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface ValidOrder {
    String message() default "Invalid Order";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
