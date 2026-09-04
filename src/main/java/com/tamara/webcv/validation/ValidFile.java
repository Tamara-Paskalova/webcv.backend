package com.tamara.webcv.validation;

import jakarta.validation.Payload;

public @interface ValidFile {

    String message() default "{projects.validation.file.unsafe}";

    Class<?>[] groups() default {} ;
    Class<? extends Payload>[] payload() default {};
}
