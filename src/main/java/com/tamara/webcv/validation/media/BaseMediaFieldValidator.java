package com.tamara.webcv.validation.media;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseMediaFieldValidator<A extends Annotation> implements ConstraintValidator<A, MultipartFile> {

    private long maxSize;
    private Set<String> allowedTypes;

    protected void configure(long maxSize, String[] allowedTypes) {
        this.maxSize = maxSize;
        this.allowedTypes = Stream.of(allowedTypes)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null) {
            return true;
        }

        if (file.isEmpty()) {
            return false;
        }

        if (file.getSize() > maxSize) {
            return false;
        }

        return isAllowedContentType(file.getContentType()) || isAllowedExtension(file.getOriginalFilename());
    }

    private boolean isAllowedContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        String cleanType = contentType.toLowerCase();
        return allowedTypes.contains(cleanType) ||
                allowedTypes.stream().anyMatch(cleanType::contains);
    }

    private boolean isAllowedExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return false;
        }

        String extension = filename.substring(dotIndex + 1).toLowerCase();
        return allowedTypes.contains(extension);
    }
}
