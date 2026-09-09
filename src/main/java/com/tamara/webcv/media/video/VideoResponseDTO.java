package com.tamara.webcv.media.video;

import com.tamara.webcv.core.MediaMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDTO {
    private String videoUrl;
    private MediaMetadata mediaMetadata;
}
