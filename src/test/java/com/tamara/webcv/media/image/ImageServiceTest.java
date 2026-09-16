package com.tamara.webcv.media.image;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository repository;

    @InjectMocks
    private ImageService service;

    @Test
    void getTargetFolderReturnsCorrectFolderName() {
        String folder = service.getTargetFolder();
        assertEquals(Constants.STORAGE_IMAGE_FOLDER_NAME, folder);
    }

    @Test
    void supportsReturnsTrueForImageType() {
        assertTrue(service.supports(Constants.MEDIA_TYPE_IMAGE));
        assertFalse(service.supports("application/pdf"));
        assertFalse(service.supports(null));
    }

    @Test
    void getMediaTypeReturnsImageType() {
        assertEquals(Constants.MEDIA_TYPE_IMAGE, service.getMediaType());
    }

    @Test
    void prepareEntityForUploadReturnsSameEntityWithoutChanges() {
        Image image = new Image();
        MultipartFile mockFile = mock(MultipartFile.class);

        Image result = service.prepareEntityForUpload(mockFile, image);

        assertSame(image, result);
    }

    @Test
    void getDtoReturnsCorrectImageResponseDto() {
        Image image = new Image();
        image.setId(42L);
        MediaMetadata metadata = new MediaMetadata();
        image.setMediaMetadata(metadata);

        String fileUrl = "https://storage.com";

        ImageResponseDTO imageDto = service.getDTO(image, fileUrl);

        assertInstanceOf(ImageResponseDTO.class, imageDto);

        assertEquals(42L, imageDto.getId());
        assertEquals(fileUrl, imageDto.getFileUrl());
        assertEquals(metadata, imageDto.getMediaMetadata());
    }

    @Test
    void getOldOrCreateNewEntityReturnsExistingEntityWhenIdIsProvidedAndFound() {
        Long existingId = 100L;
        Image existingImage = new Image();
        existingImage.setId(existingId);

        when(repository.findById(existingId)).thenReturn(Optional.of(existingImage));

        Image result = service.getOldOrCreateNewEntity(existingId);

        assertNotNull(result);
        assertEquals(existingId, result.getId());
        verify(repository).findById(existingId);
    }

    @Test
    void getOldOrCreateNewEntityReturnsNewEntityWhenIdIsProvidedButNotFound() {
        Long nonExistingId = 404L;
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Image result = service.getOldOrCreateNewEntity(nonExistingId);

        assertNotNull(result);
        assertNull(result.getId());
        verify(repository).findById(nonExistingId);
    }

    @Test
    void getOldOrCreateNewEntityReturnsNewEntityWhenIdIsNull() {
        Image result = service.getOldOrCreateNewEntity(null);

        assertNotNull(result);
        assertNull(result.getId());
        verifyNoInteractions(repository);
    }
}
