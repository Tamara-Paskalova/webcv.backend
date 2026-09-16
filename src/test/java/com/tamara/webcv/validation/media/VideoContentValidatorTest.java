package com.tamara.webcv.validation.media;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.video.VideoRequestDTO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoContentValidatorTest {

    private VideoContentValidator validator;
    private final Path stubPath = Path.of("dummy-video.mp4");

    @BeforeEach
    void setUp() {
        this.validator = new VideoContentValidator();
    }

    @Test
    @DisplayName("1: Positive scenario. Valid H.264 video should pass validation.")
    void validateContentValidVideoShouldPass(@TempDir Path tempDir) throws IOException {
        Path tempVideoPath = tempDir.resolve("temp-valid-video.mp4");

        try (InputStream is = getClass().getResourceAsStream("/test-media/valid-video.mp4")) {
            Assertions.assertNotNull(is, "Can't find file 'valid-video.mp4' in src/test/resources/test-media/");
            Files.copy(is, tempVideoPath);
        }

        ValidationResult result = validator.validateContent(tempVideoPath);

        Assertions.assertNotNull(result);
        Assertions.assertInstanceOf(ValidationResult.Visual.class, result);

        ValidationResult.Visual visualResult = (ValidationResult.Visual) result;
        assertEquals(1280, visualResult.width());
        assertEquals(720, visualResult.height());
    }

    @Test
    @DisplayName("2: Negative scenario. Corrupted video file should not pass.")
    void validateContentCorruptedVideoShouldNotPass(@TempDir Path tempDir) throws IOException {
        Path tempCorruptedPath = tempDir.resolve("temp-corrupted-video.mp4");

        Files.writeString(tempCorruptedPath, "NOT_A_VIDEO_FILE_STRUCTURE_DUMMY_DATA");

        InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                validator.validateContent(tempCorruptedPath)
        );

        assertEquals("Invalid or corrupted video.", exception.getMessage());
    }

    @Test
    @DisplayName("3: Negative scenario. Video with missing video format should not pass.")
    void validateContentThrowsExceptionWhenFormatIsMissing() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn(null);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Unable to determine video format.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("4: Negative scenario. Video with unsupported video format should not pass.")
    void validateContentThrowsExceptionWhenFormatIsUnsupported() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("avi");
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertTrue(exception.getMessage().contains("Unsupported video format"));
        }
    }

    @Test
    @DisplayName("5: Negative scenario. Video with missing video stream should not pass.")
    void validateContentThrowsExceptionWhenNoVideoStreamPresent() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("mp4");
                    when(mock.getVideoStream()).thenReturn(-1);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("File does not contain a video stream.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("6: Negative scenario. Video with too small resolution should not pass.")
    void validateContentThrowsExceptionWhenResolutionIsTooLow() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("mp4");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(640);
                    when(mock.getImageHeight()).thenReturn(480);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Video resolution must be at least 1280x720.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("7: Negative scenario. Video with unsupported codec should not pass.")
    void validateContentThrowsExceptionWhenCodecIsUnsupported() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("mp4");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(1920);
                    when(mock.getImageHeight()).thenReturn(1080);
                    when(mock.getVideoCodecName()).thenReturn("mpeg4");
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertTrue(exception.getMessage().contains("Unsupported video codec"));
        }
    }

    @Test
    @DisplayName("8: Negative scenario. Too long video should not pass.")
    void validateContentThrowsExceptionWhenDurationExceedsLimit() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("mp4");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(1280);
                    when(mock.getImageHeight()).thenReturn(720);
                    when(mock.getVideoCodecName()).thenReturn("h264");
                    when(mock.getLengthInTime()).thenReturn(660_000_000L);
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Video cannot exceed 10 minutes.", exception.getMessage());
        }
    }

    @Test
    @DisplayName("9: Negative scenario. Video with missing decodable frames should not pass.")
    void validateContentThrowsExceptionWhenVideoContainsNoDecodableFrames() {
        try (MockedConstruction<FFmpegFrameGrabber> mocked = Mockito.mockConstruction(FFmpegFrameGrabber.class,
                (mock, context) -> {
                    when(mock.getFormat()).thenReturn("mp4");
                    when(mock.getVideoStream()).thenReturn(0);
                    when(mock.getImageWidth()).thenReturn(1280);
                    when(mock.getImageHeight()).thenReturn(720);
                    when(mock.getVideoCodecName()).thenReturn("h264");
                    when(mock.getLengthInTime()).thenReturn(5_000_000L);

                    try {
                        when(mock.grab()).thenReturn(null);
                    } catch (Exception ignored) {
                    }
                })) {

            InvalidMediaException exception = assertThrows(InvalidMediaException.class, () ->
                    validator.validateContent(stubPath)
            );
            assertEquals("Video contains no decodable frames.", exception.getMessage());
        }
    }

    @Test
    void getTempFilePrefixReturnsCorrectValue() {
        String tempFilePrefix = validator.getTempFilePrefix();
        assertEquals("video-validation-", tempFilePrefix);
    }

    @Test
    void createRequestDtoReturnsValidVideoRequestDto() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "video.mp4", "video/mp4", new byte[] {});

        MediaRequestDTO dto = validator.createRequestDTO(file);

        Assertions.assertInstanceOf(VideoRequestDTO.class, dto);
        assertEquals(Constants.MEDIA_TYPE_VIDEO, dto.getContentType());
        assertEquals("video.mp4", dto.getFile().getOriginalFilename());
    }
}
