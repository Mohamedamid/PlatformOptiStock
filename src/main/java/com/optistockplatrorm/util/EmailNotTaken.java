package com.optistockplatrorm.util;

import java.lang.annotation.*;
import jakarta.validation.Payload;
import jakarta.validation.Constraint;

@Documented
@Constraint(validatedBy = EmailNotTakenValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailNotTaken {
    String message() default "This email address is already in use.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
