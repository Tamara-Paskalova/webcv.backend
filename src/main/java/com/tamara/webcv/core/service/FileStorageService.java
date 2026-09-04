package com.tamara.webcv.core.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String fileKey);

    String getFileUrl(String fileKey);
}
