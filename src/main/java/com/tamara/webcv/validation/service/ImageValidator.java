package com.tamara.webcv.validation.service;

import com.tamara.webcv.exception.InvalidMediaException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Component
public class ImageValidator implements MediaValidator {

    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 400;

    private static final Set<String> ALLOWED_FORMATS =
            Set.of("jpeg", "jpg", "png", "webp");

    @Override
    public void validate(MultipartFile file) {

        Path tempFile = null;

        try {
            tempFile = createTempFile(file);

            validateTempFile(tempFile);

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException(
                    "Invalid or corrupted image",
                    e
            );

        } finally {
            deleteTempFile(tempFile);
        }
    }

    private Path createTempFile(
            MultipartFile file
    ) throws IOException {

        Path tempFile = Files.createTempFile(
                "image-validation-",
                getExtension(file.getOriginalFilename())
        );

        file.transferTo(tempFile);

        return tempFile;
    }

    private void validateTempFile(Path file) {

        try (FFmpegFrameGrabber grabber =
                     new FFmpegFrameGrabber(file.toFile())) {

            grabber.start();

            validateFormat(grabber);
            validateImage(grabber);
            validateDecoding(grabber);

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

    private void validateImage(
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

    private String getExtension(String filename) {

        if (filename == null || filename.isBlank()) {
            return ".tmp";
        }

        int dotIndex =
                filename.lastIndexOf('.');

        if (dotIndex < 0
                || dotIndex == filename.length() - 1) {
            return ".tmp";
        }

        return filename.substring(dotIndex);
    }

    private void deleteTempFile(Path tempFile) {

        if (tempFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException ignored) {
            // Cleanup failure is intentionally ignored.
        }
    }
}
