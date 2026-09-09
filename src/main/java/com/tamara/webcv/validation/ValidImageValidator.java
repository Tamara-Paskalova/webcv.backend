package com.tamara.webcv.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private long maxSizeBytes;
    private Set<String> allowedContentTypes;

    @Override
    public void initialize(ValidImage annotation) {
        maxSizeBytes = annotation.maxSizeBytes();
        allowedContentTypes = Set.of(annotation.allowedContentTypes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        if (file.getSize() > maxSizeBytes) {
            return false;
        }

        String fileType = file.getContentType();
        return fileType != null && allowedContentTypes.contains(fileType.toLowerCase());
    }
}
