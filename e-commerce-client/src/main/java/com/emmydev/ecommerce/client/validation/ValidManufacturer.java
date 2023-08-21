package com.emmydev.ecommerce.client.validation;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidManufacturer {
    String message() default "Invalid Manufacturer";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
