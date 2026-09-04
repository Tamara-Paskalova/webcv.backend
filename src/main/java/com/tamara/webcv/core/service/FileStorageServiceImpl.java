package com.tamara.webcv.core.service;

import com.tamara.webcv.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final String filePath = "uploads";

    @Value("${supabase.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        String fileName = file.getOriginalFilename();
        String randomUUID = UUID.randomUUID().toString();
        final String uniqueName = "%s/%s/%s-%s".formatted(filePath, folder, randomUUID, fileName);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(file.getContentType())
                .key(uniqueName)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Successfully uploaded file {}", uniqueName);
            return uniqueName;
        } catch (IOException | SdkException e) {
            throw new FileStorageException("Failed to upload file %s.".formatted(uniqueName), e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try {
            s3Client.deleteObject(request);
            log.info("Successfully deleted file {}", fileKey);
        } catch (SdkException e) {
            throw new FileStorageException("Failed to delete file %s.".formatted(fileKey), e);
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(4))
                .getObjectRequest(request)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignedRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
