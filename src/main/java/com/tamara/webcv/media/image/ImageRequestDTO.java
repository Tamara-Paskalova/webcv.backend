package com.tamara.webcv.media.image;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.validation.media.ValidImage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDTO implements MediaRequestDTO {

    private final String contentType = Constants.MEDIA_TYPE_IMAGE;

    @ValidImage
    @NotNull
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
