package com.tamara.webcv.media.cv;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.validation.media.ValidDocument;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequestDTO implements MediaRequestDTO {

    private final String contentType = Constants.MEDIA_TYPE_DOCUMENT;

    @NotNull
    @ValidDocument
    private MultipartFile file;

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public MultipartFile getFile() {
        return this.file;
    }
}
