package com.tamara.webcv.validation;

import jakarta.validation.Payload;

import java.util.Set;

public @interface ValidImage {

    String message() default "{projects.validation.file.unsafe}";

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
