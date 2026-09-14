package com.tamara.webcv.media.cv;

import com.tamara.webcv.media.MediaMetadata;
import com.tamara.webcv.media.MediaResponseDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class DocumentResponseDTO extends MediaResponseDTO {

    private int version;

    public DocumentResponseDTO(long id, MediaMetadata mediaMetadata, String fileUrl, int version) {
        super(id, mediaMetadata, fileUrl);

        this.version = version;
    };
}
