package com.tamara.webcv.validation.service;

import com.tamara.webcv.exception.InvalidMediaException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;

@Component
public class VideoValidator implements MediaValidator {

    private static final Duration MAX_DURATION =
            Duration.ofMinutes(10);
    private static final int MIN_WIDTH = 1280;
    private static final int MIN_HEIGHT = 720;
    private static final Set<String> ALLOWED_FORMATS =
            Set.of("mp4", "mov");
    private static final Set<String> ALLOWED_VIDEO_CODECS =
            Set.of("h264", "hevc");

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
                    "Invalid or corrupted video",
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
                "video-validation-",
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
            validateVideoStream(grabber);
            validateVideoMetadata(grabber);
            validateDecoding(grabber);

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException(
                    "Invalid or corrupted video",
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
                    "Unable to determine video format"
            );
        }

        String normalized = format.toLowerCase();

        boolean allowed =
                ALLOWED_FORMATS.stream()
                        .anyMatch(normalized::contains);

        if (!allowed) {
            throw new InvalidMediaException(
                    "Unsupported video format: " + format
            );
        }
    }

    private void validateVideoStream(
            FFmpegFrameGrabber grabber
    ) {

        if (grabber.getVideoStream() < 0) {
            throw new InvalidMediaException(
                    "File does not contain a video stream"
            );
        }

        if (grabber.getImageWidth() <= 0
                || grabber.getImageHeight() <= 0) {

            throw new InvalidMediaException(
                    "Invalid video resolution"
            );
        }
    }

    private void validateVideoMetadata(
            FFmpegFrameGrabber grabber
    ) {

        validateResolution(grabber);
        validateCodec(grabber);
        validateDuration(grabber);
    }

    private void validateResolution(
            FFmpegFrameGrabber grabber
    ) {

        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();

        if (width < MIN_WIDTH
                || height < MIN_HEIGHT) {

            throw new InvalidMediaException(
                    "Video resolution must be at least 1280x720"
            );
        }
    }

    private void validateCodec(
            FFmpegFrameGrabber grabber
    ) {

        String videoCodec =
                grabber.getVideoCodecName();

        if (videoCodec == null
                || !ALLOWED_VIDEO_CODECS.contains(
                videoCodec.toLowerCase()
        )) {

            throw new InvalidMediaException(
                    "Unsupported video codec: "
                            + videoCodec
            );
        }
    }

    private void validateDuration(
            FFmpegFrameGrabber grabber
    ) {

        long durationMicros =
                grabber.getLengthInTime();

        if (durationMicros <= 0) {
            throw new InvalidMediaException(
                    "Unable to determine video duration"
            );
        }

        Duration duration =
                Duration.ofNanos(
                        durationMicros * 1_000
                );

        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new InvalidMediaException(
                    "Video cannot exceed 10 minutes"
            );
        }
    }

    private void validateDecoding(
            FFmpegFrameGrabber grabber
    ) throws Exception {

        int decodedFrames = 0;

        Frame frame;

        while ((frame = grabber.grab()) != null) {

            if (frame.image != null) {
                decodedFrames++;
            }
        }

        if (decodedFrames == 0) {
            throw new InvalidMediaException(
                    "Video contains no decodable frames"
            );
        }
    }

    private String getExtension(String filename) {

        if (filename == null || filename.isBlank()) {
            return ".tmp";
        }

        int dotIndex = filename.lastIndexOf('.');

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