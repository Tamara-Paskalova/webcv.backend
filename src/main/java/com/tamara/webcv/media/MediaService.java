package com.tamara.webcv.media;

import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.validation.media.ValidationResult;
import com.tamara.webcv.validation.media.BaseMediaContentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public abstract class MediaService<T extends MediaEntity> {

    @Autowired
    private BaseMediaContentValidator<T> businessValidator;

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private MediaRepository<T> repository;

    protected abstract String getTargetFolder();

    protected abstract boolean supports(String contentType);

    protected abstract T prepareEntityForUpload(MultipartFile file, T oldEntity);
    protected abstract String getMediaType();
    protected abstract MediaResponseDTO getDTO(T entity, String fileUrl);
    protected abstract T getOldOrCreateNewEntity(Long id);

    public MediaResponseDTO saveOrUpdate(MultipartFile file, Long id) {
        T savedEntity = internalSaveOrUpdate(file, id, null);
        return buildResponseDTO(savedEntity);
    }

    @Transactional
    public T internalSaveOrUpdate(MultipartFile file, Long id, String folder) {
        ValidationResult validationResult = businessValidator.validate(file);

        folder = folder == null ? getTargetFolder() : folder;

        String uniqueFileName = storageService.uploadFile(file, folder);
        T entity = getOldOrCreateNewEntity(id);
        String oldFileName = entity.getUniqueFileName();


        entity.setUniqueFileName(uniqueFileName);
        entity.setMediaMetadata(buildEntityMetadata(file, validationResult));
        T entityReadyToSave = prepareEntityForUpload(file, entity);

        T savedEntity;

        try {
            savedEntity = repository.save(entityReadyToSave);
        } catch (Exception e) {
            storageService.deleteFile(uniqueFileName);
            throw e;
        }

        if (oldFileName != null && !oldFileName.equals(uniqueFileName)) {
            storageService.deleteFile(oldFileName);
        }

        return savedEntity;
    }

    private MediaMetadata buildEntityMetadata(MultipartFile file,ValidationResult validationResult) {
        return new MediaMetadata(
                getMediaType(),
                file.getSize(),
                file.getContentType(),
                file.getOriginalFilename(),
                validationResult
        );
    }

    public void delete(T media) {
        String uniqueFileName = media.getUniqueFileName();
        repository.delete(media);
        storageService.deleteFile(uniqueFileName);

    }

    public MediaResponseDTO buildResponseDTO(T media) {
        String fileName = media.getUniqueFileName();
        String fileUrl = storageService.getFileUrl(fileName);
        return getDTO(media, fileUrl);
    }

    public List<MediaResponseDTO> getAll(String type) {
        List<T> entities = repository.findAllByMetadataType(type);

        return entities.stream()
                .map(this::buildResponseDTO)
                .toList();
    }

}
