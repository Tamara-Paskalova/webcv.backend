package com.tamara.webcv.validation.service;

import com.tamara.webcv.media.MediaEntity;
import com.tamara.webcv.media.MediaRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public abstract class MediaValidator<T extends MediaEntity> {

    @Autowired
    private Validator fieldValidator;

    protected abstract MediaRequestDTO createRequestDTO(MultipartFile file);

    protected abstract ValidationResult validateContent(MultipartFile file);

    public ValidationResult validate(MultipartFile file) {
        MediaRequestDTO requestDTO = createRequestDTO(file);
        Set<ConstraintViolation<MediaRequestDTO>> violations = fieldValidator.validate(requestDTO);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return validateContent(file);
    }
}
