package com.tamara.webcv.validation.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaValidator {
    void validate(MultipartFile file);
}
