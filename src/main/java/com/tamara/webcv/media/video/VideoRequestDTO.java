package com.tamara.webcv.media.video;

import com.tamara.webcv.core.Constants;
import com.tamara.webcv.media.MediaRequestDTO;
import com.tamara.webcv.validation.ValidVideo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequestDTO implements MediaRequestDTO {
    private String contentType = Constants.MEDIA_TYPE_VIDEO;

    @ValidVideo
    @NotNull(message = "{validation.video.notnull}")
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
