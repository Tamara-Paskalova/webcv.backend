package com.tamara.webcv.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SafeStringListValidator implements ConstraintValidator<SafeStringList, List<String>> {

    @Autowired
    private SafeTextValidator textValidator;

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {

        boolean isMalicious = value.stream()
                .anyMatch(text -> !textValidator.isValid(text, null));

        return !isMalicious;
    }
}
