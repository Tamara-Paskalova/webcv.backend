package com.tamara.webcv.validation;

import jakarta.validation.Payload;

public @interface SafeStringList {
    String message() default "{projects.validation.tags.unsafe}";

    Class<?>[] groups() default {} ;
    Class<? extends Payload>[] payload() default {};
}
