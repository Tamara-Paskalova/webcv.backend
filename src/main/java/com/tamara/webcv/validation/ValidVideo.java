package com.tamara.webcv.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidVideoValidator.class)
public @interface ValidVideo {
    String message() default "{validation.video.notnull}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long maxSizeBytes() default 100 * 1024 * 1024;

    String[] allowedContentTypes() default {
            "mp4",
            "quicktime",
            "webm"
    };
}
