package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.video.Video;
import com.tamara.webcv.media.video.VideoRequestDTO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;

@Component
public class VideoContentValidator extends BaseMediaContentValidator<Video> {

    private static final Duration MAX_DURATION =
            Duration.ofMinutes(10);
    private static final int MIN_WIDTH = 1280;
    private static final int MIN_HEIGHT = 720;
    private static final String TEMP_FILE_PRIFIX = "video-validation-";
    private static final Set<String> ALLOWED_FORMATS =
            Set.of("mp4", "mov");
    private static final Set<String> ALLOWED_VIDEO_CODECS =
            Set.of("h264", "hevc");

    @Override
    protected VideoRequestDTO createRequestDTO(MultipartFile file) {
        return new VideoRequestDTO(file);
    }

    @Override
    protected ValidationResult validateContent(Path tempFile) {

        try {

            return validateTempFile(tempFile);

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException("Invalid or corrupted video.", e);

        }
    }

    @Override
    protected String getTempFilePrefix() {
        return TEMP_FILE_PRIFIX;
    }

    private ValidationResult validateTempFile(Path file) {

        try (FFmpegFrameGrabber grabber =
                     new FFmpegFrameGrabber(file.toFile())) {

            grabber.start();

            validateFormat(grabber);
            validateVideoStream(grabber);
            ValidationResult validationResult = validateVideoMetadata(grabber);
            validateDecoding(grabber);

            return validationResult;

        } catch (InvalidMediaException e) {
            throw e;

        } catch (Exception e) {
            throw new InvalidMediaException(
                    "Invalid or corrupted video.",
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
                    "Unable to determine video format."
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
                    "File does not contain a video stream."
            );
        }

        if (grabber.getImageWidth() <= 0
                || grabber.getImageHeight() <= 0) {

            throw new InvalidMediaException(
                    "Invalid video resolution."
            );
        }
    }

    private ValidationResult validateVideoMetadata(FFmpegFrameGrabber grabber) {

        ValidationResult validationResult = validateResolution(grabber);
        validateCodec(grabber);
        validateDuration(grabber);

        return validationResult;
    }

    private ValidationResult validateResolution(FFmpegFrameGrabber grabber) {

        int width = grabber.getImageWidth();
        int height = grabber.getImageHeight();

        if (width < MIN_WIDTH
                || height < MIN_HEIGHT) {

            throw new InvalidMediaException(
                    "Video resolution must be at least 1280x720."
            );
        }

        return new ValidationResult.Visual(width, height);
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
                    "Unable to determine video duration."
            );
        }

        Duration duration =
                Duration.ofNanos(
                        durationMicros * 1_000
                );

        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new InvalidMediaException(
                    "Video cannot exceed 10 minutes."
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
                    "Video contains no decodable frames."
            );
        }
    }
}