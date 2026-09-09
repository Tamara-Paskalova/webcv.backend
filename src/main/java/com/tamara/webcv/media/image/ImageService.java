package com.tamara.webcv.media.image;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.MediaMetadata;
import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.media.MediaResponseDTO;
import com.tamara.webcv.media.MediaService;
import com.tamara.webcv.validation.service.ImageValidator;
import com.tamara.webcv.validation.service.MediaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService implements MediaService<Image> {
    private final String folder = "images";

    @Autowired
    private  FileStorageService storageService;

    @Autowired
    private ImageRepository repository;

    @Override
    public boolean supports(String contentType) {
        return Constants.MEDIA_TYPE_IMAGE.equals(contentType);
    }

    public Image uploadFile(MultipartFile file, Image image) {
        Long id = image.getId();
        String imagePathKey = image.getPathKey();
        storageService.deleteFile(imagePathKey);
        repository.deleteById(id);
        return uploadFile(file);
    }

    @Override
    public Image uploadFile(MultipartFile file) {
        MediaValidator validator = new ImageValidator();

        validator.validate(file);

        String filePathKey = storageService.uploadFile(file, folder);

        MediaMetadata metadata = getMetadata(file);

        Image image = new Image();
        image.setPathKey(filePathKey);
        image.setMediaMetadata(metadata);

        Image saved;

        try {
            saved = repository.save(image);
        } catch (Exception e) {
            storageService.deleteFile(filePathKey);
            throw e;
        }

        return saved;
    }

    @Override
    public void delete(Image media) {
        String filePathKey = media.getPathKey();
        storageService.deleteFile(filePathKey);
        repository.delete(media);
    }

    @Override
    public MediaResponseDTO getMedia(Image media) {
        if (media != null && media.getPathKey() != null) {
            String fileUrl = storageService.getFileUrl(media.getPathKey());
            return new ImageResponseDTO(media.getId(), media.getMediaMetadata(), fileUrl);
        }
        return null;
    }

    private MediaMetadata getMetadata(MultipartFile file) {
        return new MediaMetadata(Constants.MEDIA_TYPE_IMAGE, file.getSize(), file.getContentType(), file.getOriginalFilename());
    }
}
