package com.tamara.webcv.media.video;

import com.tamara.webcv.validation.ValidVideo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequestDTO {
    @ValidVideo
    @NotNull(message = "{validation.video.notnull}")
    private MultipartFile file;
}
