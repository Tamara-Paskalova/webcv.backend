package com.tamara.webcv.validation.media;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.image.Image;
import com.tamara.webcv.media.image.ImageRequestDTO;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageContentValidatorTest {

    private final BaseMediaContentValidator<Image> validator;

    public ImageContentValidatorTest() { this.validator = new ImageContentValidator(); }


    @Test
    @Description("1: Positive scenario. Valid WEBP image should pass validation.")
    void validateContentValidImageShouldPass() {
        Path path = Path.of("src/test/resources/test-media/valid-image.webp");

        ValidationResult result = validator.validateContent(path);

        assertNotNull(result);
        assertInstanceOf(ValidationResult.Visual.class, result);

        ValidationResult.Visual visualResult = (ValidationResult.Visual) result;
        assertEquals(300, visualResult.height());
        assertEquals(400, visualResult.width());
    }

    @Test
    @Description("2: Negative scenario. Invalid WEBP file should not pass validation.")
    void validateContentInvalidFileShouldNotPass() {
        Path path = Path.of("src/test/resources/test-media/corrupted-image.webp");

        assertThrows(InvalidMediaException.class, () -> validator.validateContent(path));
    }

    @Test
    void createRequestDTO() {
        MockMultipartFile file = new MockMultipartFile("image", "image.webp", "image/webp", new byte[] {});

        MediaRequestDTO dto = validator.createRequestDTO(file);

        assertInstanceOf(ImageRequestDTO.class, dto);

        assertEquals(Constants.MEDIA_TYPE_IMAGE, dto.getContentType());
        assertEquals("image.webp", dto.getFile().getOriginalFilename());
    }

    @Test
    void getTempFilePrefix() {
        assertEquals("image-validation-", validator.getTempFilePrefix());
    }
}