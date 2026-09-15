package com.tamara.webcv.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SafeSkillsMapValidator implements ConstraintValidator<SafeSkillsMap, Map<String, List<String>>> {
    private static final int MAX_TEXT_LENGTH =  100;
    private static final int MAX_KEY_LENGTH = 10;

    @Autowired
    private SafeTextValidator textValidator;

    @Override
    public boolean isValid(Map<String, List<String>> value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        boolean isTooLongKeys = value.keySet().stream()
                .filter(Objects::nonNull)
                .anyMatch((key) -> key.length() > MAX_KEY_LENGTH);

        if (isTooLongKeys) {
            return false;
        }

        boolean isTooLongValues = value.values().stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .anyMatch(text -> text.length() > MAX_TEXT_LENGTH);

        if (isTooLongValues) {
            return false;
        }

        boolean isMalicious = value.values().stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .anyMatch(text -> !textValidator.isValid(text, null));

        return !isMalicious;
    }
}
