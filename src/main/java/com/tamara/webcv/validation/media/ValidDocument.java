package com.tamara.webcv.validation.media;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDocumentFieldValidator.class)
public @interface ValidDocument {
    String message() default "{validation.document.invalid}";

    Class<?>[] groups() default {} ;
    Class<? extends Payload>[] payload() default {};

    long maxSizeBytes() default 2L * 1024 * 1024;

    String[] allowedContentTypes() default {
            "pdf",
            "application/pdf"
    };
}
