package com.tamara.webcv.validation.media;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.image.ImageRequestDTO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageContentValidatorTest {

    private ImageContentValidator validator;
    private final Path stubPath = Path.of("dummy-image.webp");

    @BeforeEach
    void setUp() {
        this.validator = new ImageContentValidator();
    }

    @Test
    @DisplayName("1: Positive scenario. Valid WEBP image should pass validation.")
    void validateContentValidImageShouldPass() {
        Path path = Path.of("src/test/resources/test-media/valid-image.png");

        ValidationResult result = validator.validateContent(path);

        assertNotNull(result);
        assertInstanceOf(ValidationResult.Visual.class, result);

        ValidationResult.Visual visualResult = (ValidationResult.Visual) result;
        assertEquals(512, visualResult.height());
        assertEquals(768, visualResult.width());

    }

    @Test
    @DisplayName("2: Negative scenario. Invalid WEBP file should not pass validation.")
    void validateContentInvalidFileShouldNotPass() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> doThrow(new RuntimeException("Native FFmpeg format open failure"))
                        .when(mock).start())) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );

            assertEquals("Invalid or corrupted image", exception.getMessage());
        }
    }

    @Test
    @DisplayName("3: Negative scenario. Missing image format should fail validation.")
    void validateContentThrowsExceptionWhenFormatIsMissing() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> when(mock.getFormat()).thenReturn(null))) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Unable to determine image format", exception.getMessage());
        }
    }

    @Test
    @DisplayName("4: Negative scenario. Unsupported format (like GIF) should fail validation.")
    void validateContentThrowsExceptionWhenFormatIsUnsupported() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> when(mock.getFormat()).thenReturn("gif"))) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertTrue(exception.getMessage().contains("Unsupported image format"));
        }
    }

    @Test
    @DisplayName("5: Negative scenario. File without a video/graphics stream should fail validation.")
    void validateContentThrowsExceptionWhenNoVideoStreamPresent() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("webp");
                    when(mock.getVideoStream()).thenReturn(-1);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("File does not contain an image", exception.getMessage());
        }
    }

    @Test
    @DisplayName("6: Negative scenario. Zero or negative image dimensions should fail validation.")
    void validateContentThrowsExceptionWhenDimensionsAreInvalid() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("webp");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(0);
                    when(mock.getImageHeight()).thenReturn(-1);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Invalid image dimensions", exception.getMessage());
        }
    }

    @Test
    @DisplayName("7: Negative scenario. Image resolution below minimum specs should fail validation.")
    void validateContentThrowsExceptionWhenResolutionIsTooLow() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("webp");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(399);
                    when(mock.getImageHeight()).thenReturn(299);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Image resolution must be at least 640x480", exception.getMessage());
        }
    }

    @Test
    @DisplayName("8: Negative scenario. Corrupted image frame that cannot be decoded should fail validation.")
    void validateContentThrowsExceptionWhenImageCannotBeDecoded() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("webp");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(640);
                    when(mock.getImageHeight()).thenReturn(480);

                    try {
                        when(mock.grab()).thenReturn(null);
                    } catch (Exception ignored) {}
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Image cannot be decoded", exception.getMessage());
        }
    }

    @Test
    @DisplayName("9: Structural check. Verify proper ImageRequestDTO conversion behavior.")
    void createRequestDto() {
        MockMultipartFile file = new MockMultipartFile("image", "image.webp", "image/webp", new byte[] {});

        MediaRequestDTO dto = validator.createRequestDTO(file);

        assertInstanceOf(ImageRequestDTO.class, dto);
        assertEquals(Constants.MEDIA_TYPE_IMAGE, dto.getContentType());
        assertEquals("image.webp", dto.getFile().getOriginalFilename());
    }

    @Test
    @DisplayName("10: Structural check. Verify the correct temporary file prefix is generated.")
    void getTempFilePrefix() {
        assertEquals("image-validation-", validator.getTempFilePrefix());
    }
}
