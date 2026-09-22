package com.tamara.webcv.media.document;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private DocumentService service;

    @Test
    void getTargetFolderReturnsCorrectFolderName() {
        String folder = service.getTargetFolder();
        Assertions.assertEquals(Constants.STORAGE_DOCUMENT_FOLDER_NAME, folder);
    }

    @Test
    void supportsReturnsTrueForDocumentType() {
        Assertions.assertTrue(service.supports(Constants.MEDIA_TYPE_DOCUMENT));
        Assertions.assertFalse(service.supports("image/png"));
        Assertions.assertFalse(service.supports(null));
    }

    @Test
    void getMediaTypeReturnsDocumentType() {
        Assertions.assertEquals(Constants.MEDIA_TYPE_DOCUMENT, service.getMediaType());
    }

    @Test
    void prepareEntityForUploadIncrementsVersion() {
        Document document = new Document();
        document.setVersion(2);

        MultipartFile mockFile = mock(MultipartFile.class);
        Document result = service.prepareEntityForUpload(mockFile, document);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.getVersion());
    }

    @Test
    void getDtoReturnsCorrectDocumentResponseDto() {
        Document document = new Document();
        document.setVersion(5);
        MediaMetadata metadata = new MediaMetadata();
        document.setMediaMetadata(metadata);

        String fileUrl = "https://storage.com";

        MediaResponseDTO dto = service.getDTO(document, fileUrl);

        Assertions.assertInstanceOf(DocumentResponseDTO.class, dto);
        DocumentResponseDTO docDto = (DocumentResponseDTO) dto;

        Assertions.assertEquals(1L, docDto.getId());
        Assertions.assertEquals(fileUrl, docDto.getFileUrl());
        Assertions.assertEquals(5, docDto.getVersion());
        Assertions.assertEquals(metadata, docDto.getMediaMetadata());
    }

    @Test
    void getOldOrCreateNewEntityReturnsExistingEntityWhenFoundInRepository() {
        Document existingDocument = new Document();
        existingDocument.setVersion(10);

        when(repository.findById(1L)).thenReturn(Optional.of(existingDocument));

        Document result = service.getOldOrCreateNewEntity(999L);

        Assertions.assertEquals(existingDocument, result);
        Assertions.assertEquals(10, result.getVersion());
        verify(repository).findById(1L);
    }

    @Test
    void getOldOrCreateNewEntityReturnsNewEntityWhenNotFoundInRepository() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Document result = service.getOldOrCreateNewEntity(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getVersion());
        verify(repository).findById(1L);
    }
}
