package com.emmydev.ecommerce.client.validation;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {
    String message() default "Invalid Category";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};
}
