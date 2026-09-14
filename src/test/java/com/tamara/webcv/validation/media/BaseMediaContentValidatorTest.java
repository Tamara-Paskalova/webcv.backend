package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaEntity;
import com.tamara.webcv.media.MediaRequestDTO;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseMediaContentValidatorTest {

    @Mock
    private Validator fieldValidator;

    private static class DummyMediaEntity extends MediaEntity {}

    private static class TestMediaValidator extends BaseMediaContentValidator<DummyMediaEntity> {
        boolean shouldThrowException = false;
        Path capturedTempFile = null;

        @Override
        protected String getTempFilePrefix() { return "test-prefix-"; }

        @Override
        protected MediaRequestDTO createRequestDTO(MultipartFile file) {
            return Mockito.mock(MediaRequestDTO.class);
        }

        @Override
        protected ValidationResult validateContent(Path tempFile) {
            this.capturedTempFile = tempFile;
            assertTrue(Files.exists(tempFile));

            if (shouldThrowException) {
                throw new RuntimeException("Simulated heavy failure");
            }
            return new ValidationResult.Document(1);
        }
    }

    private TestMediaValidator validatorInstance;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        validatorInstance = new TestMediaValidator();
        Field field = validatorInstance.getClass().getSuperclass().getDeclaredField("fieldValidator");
        field.setAccessible(true);
        field.set(validatorInstance, fieldValidator);
    }

    @Test
    @DisplayName("1. Positive validation. Temp file is created, validated and REMOVED after validations.")
    void validateSuccessfulWorkflowShouldCleanupTemporaryFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "dummy content".getBytes());

        Mockito.when(fieldValidator.validate(Mockito.any())).thenReturn(Collections.emptySet());

        ValidationResult result = validatorInstance.validate(file);

        assertNotNull(result);
        Path pathWhereFileWasCreated = validatorInstance.capturedTempFile;
        assertNotNull(pathWhereFileWasCreated);

        assertFalse(Files.exists(pathWhereFileWasCreated), "The temp file should be REMOVED!");
    }

    @Test
    @DisplayName("2. Negative scenario. When file content validation fails, the temp file is still REMOVED.")
    void validateExceptionInContentValidationShouldStillCleanupFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "dummy content".getBytes());
        Mockito.when(fieldValidator.validate(Mockito.any())).thenReturn(Collections.emptySet());

        validatorInstance.shouldThrowException = true;

        assertThrows(InvalidMediaException.class, () -> validatorInstance.validate(file));

        Path pathWhereFileWasCreated = validatorInstance.capturedTempFile;
        assertNotNull(pathWhereFileWasCreated);

        assertFalse(Files.exists(pathWhereFileWasCreated), "File should be REMOVED even if an exception has been thrown!");
    }
}