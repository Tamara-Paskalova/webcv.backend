package com.tamara.webcv.media;

import com.tamara.webcv.media.document.DocumentService;
import com.tamara.webcv.media.image.ImageService;
import com.tamara.webcv.media.video.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceFactoryTest {

    private MediaServiceFactory factory;
    private MediaService<? extends MediaEntity> imageService;
    private MediaService<? extends MediaEntity> documentService;
    private MediaService<? extends MediaEntity> videoService;

    @BeforeEach
    void setUp() {
        imageService = mock(ImageService.class);
        documentService = mock(DocumentService.class);
        videoService = mock(VideoService.class);

        List<MediaService<? extends MediaEntity>> services = new ArrayList<>();
        services.add(imageService);
        services.add(documentService);
        services.add(videoService);

        factory = new MediaServiceFactory(services);
    }

    @Test
    void getReturnsCorrectServiceWhenTypeIsSupported() {
        String mediaType = "image";

        when(imageService.supports(mediaType)).thenReturn(true);

        MediaService<? extends MediaEntity> result = factory.get(mediaType);

        assertNotNull(result);
        assertEquals(imageService, result);
    }

    @Test
    void getThrowsBadRequestExceptionWhenTypeIsNull() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> factory.get(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Media type parameter cannot be empty", exception.getReason());

        verifyNoInteractions(imageService, documentService, videoService);
    }

    @Test
    void getThrowsBadRequestExceptionWhenTypeIsBlank() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> factory.get("   ")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Media type parameter cannot be empty", exception.getReason());
        verifyNoInteractions(imageService, documentService, videoService);
    }

    @Test
    void getThrowsBadRequestExceptionWhenTypeIsNotSupportedByAnyService() {
        String unsupportedType = "video/dummy";

        when(imageService.supports(unsupportedType)).thenReturn(false);
        when(documentService.supports(unsupportedType)).thenReturn(false);
        when(videoService.supports(unsupportedType)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> factory.get(unsupportedType)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Unsupported media type: " + unsupportedType, exception.getReason());
    }
}
