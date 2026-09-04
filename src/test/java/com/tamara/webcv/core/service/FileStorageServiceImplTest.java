package com.tamara.webcv.core.service;

import com.tamara.webcv.config.S3Config;
import com.tamara.webcv.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

    @Mock
    private S3Config config;

    @Mock
    private S3Presigner presigner;

    @Mock
    private S3Client client;

    @InjectMocks
    private FileStorageServiceImpl service;

    @Test
    void uploadFile() {
        MultipartFile file = new MockMultipartFile("PhotoName", "PhotoOriginalName",null, new byte[] {});
        String name = service.uploadFile(file, "dummyFolder");

        verify(client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertTrue(name.contains("dummyFolder"));
        assertTrue(name.contains("PhotoOriginalName"));
    }

    @Test
    void uploadFileThrowsException() {
        MultipartFile file = new MockMultipartFile("PhotoName", new byte[] {});

        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(SdkException.class);

        assertThrows(FileStorageException.class,() -> service.uploadFile(file, "folder"));
    }

    @Test
    void deleteFile() {
        service.deleteFile("some fileKey");
        verify(client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFileThrowsException() {
        when(client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(SdkException.class);

        assertThrows(FileStorageException.class, () -> service.deleteFile("some fileKey"));
    }

    @Test
    void getFileUrl() throws Exception {
        // Arrange (Подготовка данных)
        String fileKey = "avatars/user-123.jpg";
        String expectedUrlStr = "https://amazonaws.com...";

        PresignedGetObjectRequest presignedGetObjectRequestMock = Mockito.mock(PresignedGetObjectRequest.class);
        URL fakeUrl = URI.create(expectedUrlStr).toURL();

        when(presignedGetObjectRequestMock.url()).thenReturn(fakeUrl);
        when(presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedGetObjectRequestMock);

        ArgumentCaptor<GetObjectPresignRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);

        String actualUrl = service.getFileUrl(fileKey);

        assertEquals(expectedUrlStr, actualUrl);

        verify(presigner).presignGetObject(requestCaptor.capture());

        GetObjectPresignRequest capturedRequest = requestCaptor.getValue();
        assertEquals(Duration.ofHours(4), capturedRequest.signatureDuration());
        assertEquals(fileKey, capturedRequest.getObjectRequest().key());
    }
}