package com.tamara.webcv.media.image;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService extends MediaService<Image> {

    @Autowired
    private ImageRepository repository;

    @Override
    protected boolean supports(String contentType) {
        return Constants.MEDIA_TYPE_IMAGE.equals(contentType);
    }

    @Override
    protected Image prepareEntityForUpload(MultipartFile file, Image entity) {
        return entity;
    }

    @Override
    protected ImageResponseDTO getDTO(Image entity, String fileUrl) {
        return new ImageResponseDTO(entity.getId(), entity.getMediaMetadata(), fileUrl);
    }

    @Override
    protected String getMediaType() {
        return Constants.MEDIA_TYPE_IMAGE;
    }

    @Override
    protected Image getOldOrCreateNewEntity(Long id) {
        if (id != null) {
            return repository.findById(id).orElseGet(Image::new);
        }
        return new Image();
    }

    @Override
    public String getTargetFolder() {
        return Constants.STORAGE_IMAGE_FOLDER_NAME;
    }
}
