package com.tamara.webcv.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SafeTextValidatorTest {

    private SafeTextValidator validator;

    public SafeTextValidatorTest() {
        validator = new SafeTextValidator();
    }

    @Test
    void isValidReturnsTrueWhenNullPassed() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValidReturnsTrueWhenEmptyStringPassed() {
        assertTrue(validator.isValid("  ", null));
    }
}