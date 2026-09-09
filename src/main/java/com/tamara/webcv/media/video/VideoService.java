package com.tamara.webcv.media.video;


import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.MediaMetadata;
import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.validation.service.MediaValidator;
import com.tamara.webcv.validation.service.VideoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService {
    private final String folder = "video";
    private final long uniqueVideoId = 1;

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private VideoRepository repository;

    @Transactional
    public void uploadVideo(VideoRequestDTO dto) {
        MediaValidator validator = new VideoValidator();
        MultipartFile file = dto.getFile();

        validator.validate(file);

        Video video = repository.findById(uniqueVideoId)
                .orElseGet(Video::new);

        String oldVideoKey = video.getPathKey();

        String newVideoKey = storageService.uploadFile(file, folder);

        try {
            video.setPathKey(newVideoKey);
            video.setVideoVersion(video.getVideoVersion() + 1);
            video.setMediaMetadata(getMetadata(file));

            repository.save(video);

        } catch (Exception e) {
            storageService.deleteFile(newVideoKey);
            throw e;
        }

        if (oldVideoKey != null) {
            storageService.deleteFile(oldVideoKey);
        }
    }

    public VideoResponseDTO getVideo() {
        Video video = repository.findById(uniqueVideoId).orElse(null);
         if (video == null) {
             return null;
         }
        String fileUrl = storageService.getFileUrl(video.getPathKey());
        return new VideoResponseDTO(fileUrl, video.getMediaMetadata());
    }

    private MediaMetadata getMetadata(MultipartFile file) {
        return new MediaMetadata(Constants.METADATA_TYPE_VIDEO, file.getSize(), file.getContentType(), file.getOriginalFilename());
    }
}
