package com.tamara.webcv.media.video;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository repository;

    @InjectMocks
    private VideoService service;

    @Test
    @DisplayName("1: Structural check. Verify target folder matches video storage configuration.")
    void getTargetFolderReturnsCorrectFolderName() {
        String folder = service.getTargetFolder();
        assertEquals(Constants.STORAGE_VIDEO_FOLDER_NAME, folder);
    }

    @Test
    @DisplayName("2: Business rule. Support only dedicated video media type format.")
    void supportsReturnsTrueForVideoType() {
        assertTrue(service.supports(Constants.MEDIA_TYPE_VIDEO));
        assertFalse(service.supports("image/jpeg"));
        assertFalse(service.supports(null));
    }

    @Test
    @DisplayName("3: Structural check. Return valid media type identifier string.")
    void getMediaTypeReturnsVideoType() {
        assertEquals(Constants.MEDIA_TYPE_VIDEO, service.getMediaType());
    }

    @Test
    @DisplayName("4: Business rule. Increment internal video version counter prior to asset upload.")
    void prepareEntityForUploadIncrementsVideoVersion() {
        Video video = new Video();
        video.setVideoVersion(5);

        MultipartFile mockFile = mock(MultipartFile.class);
        Video result = service.prepareEntityForUpload(mockFile, video);

        assertNotNull(result);
        assertEquals(6, result.getVideoVersion());
    }

    @Test
    @DisplayName("5: Structural check. Verify valid VideoResponseDTO instantiation with fixed entity identity.")
    void getDtoReturnsCorrectVideoResponseDto() {
        Video video = new Video();
        video.setVideoVersion(3);
        MediaMetadata metadata = new MediaMetadata();
        video.setMediaMetadata(metadata);

        String fileUrl = "https://storage.com";

        MediaResponseDTO dto = service.getDTO(video, fileUrl);

        assertInstanceOf(VideoResponseDTO.class, dto);
        VideoResponseDTO videoDto = (VideoResponseDTO) dto;

        assertEquals(1L, videoDto.getId());
        assertEquals(fileUrl, videoDto.getFileUrl());
        assertEquals(3, videoDto.getVersion());
        assertEquals(metadata, videoDto.getMediaMetadata());
    }

    @Test
    @DisplayName("6: Database integration. Fetch existing static video entity when record is present in repository.")
    void getOldOrCreateNewEntityReturnsExistingEntityWhenFoundInRepository() {
        Video existingVideo = new Video();
        existingVideo.setVideoVersion(42);

        when(repository.findById(1L)).thenReturn(Optional.of(existingVideo));

        Video result = service.getOldOrCreateNewEntity(999L);

        assertEquals(existingVideo, result);
        assertEquals(42, result.getVideoVersion());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("7: Database integration. Provision a fresh video model fallback when repository lookup fails.")
    void getOldOrCreateNewEntityReturnsNewEntityWhenNotFoundInRepository() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Video result = service.getOldOrCreateNewEntity(1L);

        assertNotNull(result);
        assertEquals(0, result.getVideoVersion());
        verify(repository).findById(1L);
    }
}
