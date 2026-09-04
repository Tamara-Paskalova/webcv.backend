package com.tamara.webcv.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

public class SafeMapValidator implements ConstraintValidator<SafeMap, Map<String, Map<String, String>>> {

    private static final int MAX_TEXT_LENGTH = 1000;

    @Autowired
    private SafeTextValidator textValidator;

    @Override
    public boolean isValid(Map<String, Map<String, String>> value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        boolean isTooLong = value.values().stream()
                .filter(Objects::nonNull)
                .flatMap(innerMap -> innerMap.values().stream())
                .filter(Objects::nonNull)
                .anyMatch(text -> text.length() > MAX_TEXT_LENGTH);

        if (isTooLong) {
            return false;
        }

        boolean isMalicious = value.values().stream()
                .filter(Objects::nonNull)
                .flatMap(innerMap -> innerMap.values().stream())
                .filter(Objects::nonNull)
                .anyMatch(text -> !textValidator.isValid(text, null));

        return !isMalicious;
    }
}
