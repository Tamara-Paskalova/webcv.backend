package com.tamara.webcv.media;

import com.tamara.webcv.core.MediaMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class MediaResponseDTO {
    private long id;
    private MediaMetadata mediaMetadata;
    private String fileUrl;
}
