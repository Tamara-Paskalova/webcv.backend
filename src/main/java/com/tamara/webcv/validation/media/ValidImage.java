package com.tamara.webcv.validation.media;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageFieldValidator.class)
public @interface ValidImage {

    String message() default "{validation.image.invalid}";

    Class<?>[] groups() default {} ;
    Class<? extends Payload>[] payload() default {};

    long maxSizeBytes() default 10L * 1024 * 1024;

    String[] allowedContentTypes() default {
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    };
}
