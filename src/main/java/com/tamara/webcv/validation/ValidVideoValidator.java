package com.tamara.webcv.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ValidVideoValidator implements ConstraintValidator<ValidVideo, MultipartFile> {
    private long maxSize;
    private Set<String> allowedContentTypes;

    @Override
    public void initialize(ValidVideo annotation) {
        maxSize = annotation.maxSizeBytes();
        allowedContentTypes = Set.of(annotation.allowedContentTypes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        if (file.getSize() > maxSize) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();

        return hasAllowedExtension(originalFilename);
    }

    private boolean hasAllowedExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return false;
        }

        String extension = filename.substring(dotIndex + 1).toLowerCase();

        return allowedContentTypes.contains(extension);
    }
}
