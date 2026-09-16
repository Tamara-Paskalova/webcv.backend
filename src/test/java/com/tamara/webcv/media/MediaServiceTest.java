package com.tamara.webcv.media;

import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.validation.media.BaseMediaContentValidator;
import com.tamara.webcv.validation.media.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private BaseMediaContentValidator<TestMediaEntity> businessValidator;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MediaRepository<TestMediaEntity> repository;

    private TestMediaService mediaService;

    private MultipartFile mockFile;
    private ValidationResult stubValidationResult;
    private TestMediaEntity testEntity;

    private static class TestMediaEntity extends MediaEntity {}
    private static class TestMediaResponseDTO extends MediaResponseDTO {}

    private static class TestMediaService extends MediaService<TestMediaEntity> {
        private TestMediaEntity entityToReturn;

        public void setEntityToReturn(TestMediaEntity entityToReturn) {
            this.entityToReturn = entityToReturn;
        }

        @Override protected String getTargetFolder() { return "test-folder"; }
        @Override protected boolean supports(String contentType) { return "test-type".equals(contentType); }
        @Override protected TestMediaEntity prepareEntityForUpload(MultipartFile file, TestMediaEntity oldEntity) { return oldEntity; }
        @Override protected String getMediaType() { return "TEST_MEDIA"; }
        @Override protected MediaResponseDTO getDTO(TestMediaEntity entity, String fileUrl) { return new TestMediaResponseDTO(); }
        @Override protected TestMediaEntity getOldOrCreateNewEntity(Long id) { return entityToReturn; }
    }

    @BeforeEach
    void setUp() {
        mockFile = mock(MultipartFile.class);
        testEntity = new TestMediaEntity();

        mediaService = new TestMediaService();
        mediaService.setEntityToReturn(testEntity);

        ReflectionTestUtils.setField(mediaService, "businessValidator", businessValidator);
        ReflectionTestUtils.setField(mediaService, "storageService", storageService);
        ReflectionTestUtils.setField(mediaService, "repository", repository);

        stubValidationResult = new ValidationResult.Document(1);
    }

    @Test
    void internalSaveOrUpdateSuccessWithNewFileAndDeletesOldOne() {
        Long entityId = 1L;
        testEntity.setUniqueFileName("old-file.png");

        when(businessValidator.validate(mockFile)).thenReturn(stubValidationResult);
        when(storageService.uploadFile(mockFile, "test-folder")).thenReturn("new-unique-file.png");
        when(repository.save(any(TestMediaEntity.class))).thenReturn(testEntity);

        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/png");
        when(mockFile.getOriginalFilename()).thenReturn("avatar.png");

        TestMediaEntity result = mediaService.internalSaveOrUpdate(mockFile, entityId, null);

        assertNotNull(result);
        assertEquals("new-unique-file.png", result.getUniqueFileName());
        verify(storageService).deleteFile("old-file.png");
        verify(repository).save(testEntity);
    }

    @Test
    void internalSaveOrUpdateDeletesUploadedFileWhenRepositoryThrowsException() {
        Long entityId = 1L;

        when(businessValidator.validate(mockFile)).thenReturn(stubValidationResult);
        when(storageService.uploadFile(mockFile, "test-folder")).thenReturn("just-uploaded-file.png");
        when(repository.save(any(TestMediaEntity.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
                mediaService.internalSaveOrUpdate(mockFile, entityId, null)
        );

        verify(storageService).deleteFile("just-uploaded-file.png");
        verify(storageService, never()).deleteFile("old-file.png");
    }

    @Test
    void deleteRemovesEntityFromRepositoryAndDeletesFileFromStorage() {
        testEntity.setUniqueFileName("file-to-delete.png");

        mediaService.delete(testEntity);

        verify(repository).delete(testEntity);
        verify(storageService).deleteFile("file-to-delete.png");
    }

    @Test
    void buildResponseDtoCallsStorageForUrlAndReturnsDto() {
        testEntity.setUniqueFileName("some-file.png");
        when(storageService.getFileUrl("some-file.png")).thenReturn("https://cdn.com");

        MediaResponseDTO dto = mediaService.buildResponseDTO(testEntity);

        assertNotNull(dto);
        assertInstanceOf(TestMediaResponseDTO.class, dto);
        verify(storageService).getFileUrl("some-file.png");
    }
}
