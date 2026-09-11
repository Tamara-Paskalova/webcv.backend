package com.tamara.webcv.media.video;

import com.tamara.webcv.media.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class VideoResponseDTO extends MediaResponseDTO {

    private int version;

    public VideoResponseDTO(long id, MediaMetadata mediaMetadata, String fileUrl, int version) {
        super(id, mediaMetadata, fileUrl);

        this.version = version;
    }
}
