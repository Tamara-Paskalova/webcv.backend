package com.tamara.webcv.validation.media;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.video.Video;
import com.tamara.webcv.media.video.VideoRequestDTO;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VideoContentValidatorTest {

    private final BaseMediaContentValidator<Video> validator;

    public VideoContentValidatorTest() {
        this.validator = new VideoContentValidator();
    }

    @Test
    @Description("1: Positive scenario. Valid H.264 video should pass validation.")
    void validateContentValidVideoShouldPass() {
        Path path = Path.of("src/test/resources/test-media/valid-video.mp4");
        ValidationResult result = validator.validateContent(path);

        assertNotNull(result);
        assertInstanceOf(ValidationResult.Visual.class, result);

        ValidationResult.Visual visualResult = (ValidationResult.Visual) result;
        assertEquals(1280, visualResult.width());
        assertEquals(720, visualResult.height());
    }

    @Test
    @Description("2: Negative scenario. Corrupted video should not pass validation.")
    void validateContentCorruptedVideoShouldNotPass() {
        Path path = Path.of("src/test/resources/test-media/corrupted-video.mp4");

        InvalidMediaException exception = assertThrows(InvalidMediaException.class, () -> validator.validateContent(path));

        assertEquals("Invalid or corrupted video.", exception.getMessage());

    }


    @Test
    void getTempFilePrefix() {
        String tempFilePrefix = validator.getTempFilePrefix();
        assertEquals("video-validation-", tempFilePrefix);
    }

    @Test
    void createRequestDTO() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "video.mp4", "video/mp4", new byte[] {});

        MediaRequestDTO dto = validator.createRequestDTO(file);

        assertInstanceOf(VideoRequestDTO.class, dto);
        assertEquals(Constants.MEDIA_TYPE_VIDEO, dto.getContentType());
        assertEquals("video.mp4", dto.getFile().getOriginalFilename());
    }

}