package com.tamara.webcv.media.video;


import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.MediaMetadata;
import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.media.MediaResponseDTO;
import com.tamara.webcv.media.MediaService;
import com.tamara.webcv.validation.service.MediaValidator;
import com.tamara.webcv.validation.service.VideoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService implements MediaService<Video> {
    private final String folder = "video";
    private final long uniqueVideoId = 1;

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private VideoRepository repository;

    @Transactional
    public Video uploadFile(MultipartFile file) {
        MediaValidator validator = new VideoValidator();

        validator.validate(file);

        Video video = repository.findById(uniqueVideoId)
                .orElseGet(Video::new);

        String oldVideoKey = video.getPathKey();

        String newVideoKey = storageService.uploadFile(file, folder);

        Video saved;

        try {
            video.setPathKey(newVideoKey);
            video.setVideoVersion(video.getVideoVersion() + 1);
            video.setMediaMetadata(getMetadata(file));

            saved = repository.save(video);

        } catch (Exception e) {
            storageService.deleteFile(newVideoKey);
            throw e;
        }

        if (oldVideoKey != null) {
            storageService.deleteFile(oldVideoKey);
        }

        return saved;
    }

    @Override
    public boolean supports(String contentType) {
        return Constants.MEDIA_TYPE_VIDEO.equals(contentType);
    }

    @Override
    public void delete(Video media) {
        String fileKey = media.getPathKey();
        repository.delete(media);
        storageService.deleteFile(fileKey);
    }

    public MediaResponseDTO getMedia(Video media) {
        Video video = repository.findById(uniqueVideoId).orElse(null);
         if (video == null) {
             return null;
         }
        String fileUrl = storageService.getFileUrl(video.getPathKey());
        return new VideoResponseDTO(1, video.getMediaMetadata(), fileUrl);
    }

    private MediaMetadata getMetadata(MultipartFile file) {
        return new MediaMetadata(Constants.MEDIA_TYPE_VIDEO, file.getSize(), file.getContentType(), file.getOriginalFilename());
    }
}
