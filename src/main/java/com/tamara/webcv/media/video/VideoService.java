package com.tamara.webcv.media.video;


import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoService extends MediaService<Video> {
    private final static Long UNIQUE_VIDEO_ID = 1L;

    @Autowired
    private VideoRepository repository;

    @Override
    protected boolean supports(String contentType) {
        return Constants.MEDIA_TYPE_VIDEO.equals(contentType);
    }

    @Override
    protected Video prepareEntityForUpload(MultipartFile file, Video entity) {
        entity.setVideoVersion(entity.getVideoVersion() + 1);
        return entity;
    }

    @Override
    protected VideoResponseDTO getDTO(Video entity, String fileUrl) {
        return new VideoResponseDTO(UNIQUE_VIDEO_ID, entity.getMediaMetadata(), fileUrl, entity.getVideoVersion());
    }

    @Override
    protected String getMediaType() {
        return Constants.MEDIA_TYPE_VIDEO;
    }

    @Override
    protected Video getOldOrCreateNewEntity(Long id) {
        return repository.findById(UNIQUE_VIDEO_ID)
                .orElseGet(Video::new);
    }

    @Override
    public String getTargetFolder() {
        return Constants.STORAGE_VIDEO_FOLDER_NAME;
    }
}
