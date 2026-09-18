package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.cv.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocumentContentValidatorTest {

    private final BaseMediaContentValidator<Document> validator;

    public DocumentContentValidatorTest() {
        this.validator = new DocumentContentValidator();
    }

    @Test
    void createRequestDTO() {
        String docName = "documentName";
        String originalFilename = "document.pdf";
        String contentType = "document";
        MockMultipartFile file = new MockMultipartFile(docName, originalFilename, contentType, new byte[]{});

        MediaRequestDTO dto = validator.createRequestDTO(file);

        assertNotNull(dto);
        assertEquals(contentType, dto.getContentType());
        assertEquals(originalFilename, dto.getFile().getOriginalFilename());
        assertEquals(docName, dto.getFile().getName());
    }

    @Test
    @DisplayName("1: Positive scenario. Valid PDF document should pass validation.")
    void validateContentValidDocumentShouldPass(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("valid.pdf");

        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.addPage(new PDPage());
            document.save(pdfPath.toFile());
        }

        ValidationResult result = validator.validateContent(pdfPath);

        assertNotNull(result);
        assertInstanceOf(ValidationResult.Document.class, result);

        ValidationResult.Document documentResult = (ValidationResult.Document) result;
        assertEquals(2, documentResult.pageCount());
    }

    @Test
    @DisplayName("2: Negative scenario. Corrupted document should not pass validation.")
    void validateContentCorruptedDocumentShouldNotPass(@TempDir Path tempDir) throws IOException {
        Path corruptedPath = tempDir.resolve("corrupted.pdf");

        Files.writeString(corruptedPath, "THIS IS NOT A VALID STRUCTURE. HELLO_WORLD!!!");

        InvalidMediaException exception = assertThrows(InvalidMediaException.class, () -> validator.validateContent(corruptedPath));

        assertTrue(exception.getMessage().contains("The file is corrupted or manipulated"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause());

    }

    @Test
    @DisplayName("3: Negative scenario. Encrypted PDF file should not pass.")
    void isValidContentEncryptedShouldNotPass(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("encrypted.pdf");

        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());

            StandardProtectionPolicy policy = new StandardProtectionPolicy("test1234", "", new AccessPermission());
            policy.setEncryptionKeyLength(128);
            document.protect(policy);

            document.save(pdfPath.toFile());
        }

        InvalidMediaException exception = assertThrows(InvalidMediaException.class, () -> validator.validateContent(pdfPath));

        assertEquals("The document is password-protected or encrypted.", exception.getMessage());
    }

    @Test
    @DisplayName("4: Negative scenario. Empty PDF file should not pass.")
    void isValidContentEmptyDocumentShouldNotPass(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("emptyPDF.pdf");

        try (PDDocument document = new PDDocument()) {
            document.save(pdfPath.toFile());
        }

        InvalidMediaException exception = assertThrows(InvalidMediaException.class, () -> validator.validateContent(pdfPath));

        assertEquals("The document contains no pages.", exception.getMessage());
    }

    @Test
    void getTempFilePrefix() {
        assertEquals("document-validation-", validator.getTempFilePrefix());
    }
}