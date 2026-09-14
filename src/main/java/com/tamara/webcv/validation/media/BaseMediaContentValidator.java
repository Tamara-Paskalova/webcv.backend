package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaEntity;
import com.tamara.webcv.media.MediaRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public abstract class BaseMediaContentValidator<T extends MediaEntity> {

    @Autowired
    private Validator fieldValidator;

    protected abstract MediaRequestDTO createRequestDTO(MultipartFile file);

    protected abstract ValidationResult validateContent(Path tempFile);

    protected abstract String getTempFilePrefix();

    public ValidationResult validate(MultipartFile file) {
        MediaRequestDTO requestDTO = createRequestDTO(file);
        Set<ConstraintViolation<MediaRequestDTO>> violations = fieldValidator.validate(requestDTO);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        Path tempFile = null;

        try {
            tempFile = createTempFile(file);
            return validateContent(tempFile);
        } catch (InvalidMediaException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidMediaException("Invalid or corrupted file content.", e);
        } finally {
            deleteTempFile(tempFile);
        }
    }

    private void deleteTempFile(Path tempFile) {
        if (tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
                //Intentionally ignore temp file cleanup
            }
        }

    }

    private Path createTempFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = ".tmp";

        if (originalFileName != null) {
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex < originalFileName.length() - 1) {
                extension = originalFileName.substring(dotIndex);
            }
        }

        Path tempFile = Files.createTempFile(getTempFilePrefix(), extension);
        file.transferTo(tempFile);
        return tempFile;
    }
}
