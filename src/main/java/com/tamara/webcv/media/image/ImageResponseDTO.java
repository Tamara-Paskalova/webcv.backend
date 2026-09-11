package com.tamara.webcv.media.image;

import com.tamara.webcv.media.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class ImageResponseDTO extends MediaResponseDTO {
    public ImageResponseDTO(long id, MediaMetadata mediaMetadata, String fileUrl) {
        super(id, mediaMetadata, fileUrl);
    }
}
