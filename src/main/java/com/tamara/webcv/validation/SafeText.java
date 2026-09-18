package com.tamara.webcv.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;

@Documented
@Constraint(validatedBy = SafeTextValidator.class)
@Target({FIELD, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeText{
    String message() default "{message.validation.unsafe}";

    Class<?>[] groups() default {} ;
    Class<? extends Payload>[] payload() default {};
}
