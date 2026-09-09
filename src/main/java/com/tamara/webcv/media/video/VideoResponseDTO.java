package com.tamara.webcv.media.video;

import com.tamara.webcv.core.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class VideoResponseDTO extends MediaResponseDTO {
    public VideoResponseDTO(long id, MediaMetadata mediaMetadata, String fileUrl) {
        super(id, mediaMetadata, fileUrl);
    }
}
