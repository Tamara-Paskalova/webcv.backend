package com.tamara.webcv.media.cv;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaResponseDTO;
import com.tamara.webcv.media.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class DocumentService extends MediaService<Document> {

    private static final Long UNIQUE_DOCUMENT_ID = 1L;

    @Autowired
    private DocumentRepository repository;

    @Override
    protected String getTargetFolder() {
        return Constants.STORAGE_DOCUMENT_FOLDER_NAME;
    }

    @Override
    protected boolean supports(String contentType) {
        return Constants.MEDIA_TYPE_DOCUMENT.equals(contentType);
    }

    @Override
    protected Document prepareEntityForUpload(MultipartFile file, Document entity) {
        entity.setVersion(entity.getVersion() + 1);
        return entity;
    }

    @Override
    protected String getMediaType() {
        return Constants.MEDIA_TYPE_DOCUMENT;
    }

    @Override
    protected MediaResponseDTO getDTO(Document entity, String fileUrl) {
        return new DocumentResponseDTO(UNIQUE_DOCUMENT_ID, entity.getMediaMetadata(), fileUrl, entity.getVersion());
    }

    @Override
    protected Document getOldOrCreateNewEntity(Long id) {
        return repository.findById(UNIQUE_DOCUMENT_ID)
                .orElseGet(Document::new);
    }
}
