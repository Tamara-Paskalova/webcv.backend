package com.tamara.webcv.validation.media;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseMediaFieldValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private static class TestMediaFieldValidator extends BaseMediaFieldValidator<Annotation> {
        public void setup(long maxSize, String[] allowedTypes) {
            super.configure(maxSize, allowedTypes);
        }
    }

    private TestMediaFieldValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TestMediaFieldValidator();
        validator.setup(1024 * 1024, new String[]{"png", "pdf", "application/pdf"});
    }

    @Test
    @DisplayName("1: If file is null the validation should return true. @NotNull is responsible of such cases.")
    void isValidNullFileShouldReturnTrue() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    @DisplayName("2: Negative scenario. Should return false on empty [0 byte] file.")
    void isValidEmptyFileShouldReturnFalse() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.png", "image/png", new byte[0]);
        assertFalse(validator.isValid(emptyFile, context));
    }

    @Test
    @DisplayName("3: Should return false on too large file.")
    void isValidFileTooLargeShouldReturnFalse() {
        byte[] largeContent = new byte[1024 * 1024 + 1];
        MockMultipartFile largeFile = new MockMultipartFile("file", "test.png", "image/png", largeContent);
        assertFalse(validator.isValid(largeFile, context));
    }

    @Test
    @DisplayName("4: Positive scenario. Valid file should return true")
    void isValidValidFileShouldReturnTrue() {
        MockMultipartFile validFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[]{1, 2, 3});
        assertTrue(validator.isValid(validFile, context));
    }

    @Test
    @DisplayName("5: Negative scenario. Invalid content type or extension should return false.")
    void isValidInvalidExtensionShouldReturnFalse() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "script.exe", "application/x-msdownload", new byte[]{1, 2, 3});
        assertFalse(validator.isValid(invalidFile, context));
    }
}
