package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.image.Image;
import com.tamara.webcv.media.image.ImageRequestDTO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Set;

@Component
public class ImageContentValidator extends BaseMediaContentValidator<Image> {

    private static final int MIN_WIDTH = 640;
    private static final int MIN_HEIGHT = 480;
    private static final String TEMP_FILE_PREFIX = "image-validation-";

    private static final Set<String> ALLOWED_FORMATS =
            Set.of("jpeg", "jpg", "png", "webp");

    @Override
    protected ImageRequestDTO createRequestDTO(MultipartFile file) {
        return new ImageRequestDTO(file);
    }

    @Override
    protected ValidationResult validateContent(Path tempFile) {

        try {

            return validateTempFile(tempFile);

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException("Invalid or corrupted image", e);
        }
    }

    @Override
    protected String getTempFilePrefix() {
        return TEMP_FILE_PREFIX;
    }

    private ValidationResult validateTempFile(Path file) {

        try (FFmpegFrameGrabber grabber =
                     new FFmpegFrameGrabber(file.toFile())) {

            grabber.start();

            validateFormat(grabber);
            ValidationResult validationResult = validateImage(grabber);
            validateDecoding(grabber);

            return validationResult;

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException(
                    "Invalid or corrupted image",
                    e
            );
        }
    }

    private void validateFormat(
            FFmpegFrameGrabber grabber
    ) {

        String format = grabber.getFormat();

        if (format == null || format.isBlank()) {
            throw new InvalidMediaException(
                    "Unable to determine image format"
            );
        }

        String normalized =
                format.toLowerCase();

        boolean allowed =
                ALLOWED_FORMATS.stream()
                        .anyMatch(normalized::contains);

        if (!allowed) {
            throw new InvalidMediaException(
                    "Unsupported image format: " + format
            );
        }
    }

    private ValidationResult validateImage(
            FFmpegFrameGrabber grabber
    ) {

        if (grabber.getVideoStream() < 0) {
            throw new InvalidMediaException(
                    "File does not contain an image"
            );
        }

        int width =
                grabber.getImageWidth();

        int height =
                grabber.getImageHeight();

        if (width <= 0 || height <= 0) {
            throw new InvalidMediaException(
                    "Invalid image dimensions"
            );
        }

        if (width < MIN_WIDTH
                || height < MIN_HEIGHT) {

            throw new InvalidMediaException(
                    "Image resolution must be at least 640x480"
            );
        }

        return new ValidationResult.Visual(width, height);
    }

    private void validateDecoding(
            FFmpegFrameGrabber grabber
    ) throws Exception {

        Frame frame = grabber.grab();

        if (frame == null || frame.image == null) {
            throw new InvalidMediaException(
                    "Image cannot be decoded"
            );
        }
    }
}
