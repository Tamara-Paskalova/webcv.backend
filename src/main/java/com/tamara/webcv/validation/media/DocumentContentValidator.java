package com.tamara.webcv.validation.media;

import com.tamara.webcv.exception.InvalidMediaException;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.media.cv.Document;
import com.tamara.webcv.media.cv.DocumentRequestDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public class DocumentContentValidator extends BaseMediaContentValidator<Document> {

    private static final String TEMP_FILE_PREFIX = "document-validation-";

    @Override
    protected MediaRequestDTO createRequestDTO(MultipartFile file) {
        return new DocumentRequestDTO(file);
    }

    @Override
    protected ValidationResult validateContent(Path tempFile) {

        try (PDDocument document = Loader.loadPDF(tempFile.toFile())){
            if (document.isEncrypted()) {
                throw new InvalidMediaException("The document is password-protected or encrypted.");
            }

            int pageCount = document.getNumberOfPages();
            if (pageCount <= 0) {
                throw new InvalidMediaException("The document contains no pages.");
            }

            return new ValidationResult.Document(pageCount);

        } catch (IOException e) {
            throw new InvalidMediaException("Invalid document content. The file is corrupted or manipulated", e);
        } catch (Exception e) {
            throw new InvalidMediaException("Error occurred during document validation.");
        }
    }

    @Override
    protected String getTempFilePrefix() {
        return TEMP_FILE_PREFIX;
    }
}
