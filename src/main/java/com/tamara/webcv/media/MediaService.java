package com.tamara.webcv.media;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MediaService<T extends MediaEntity> {
    boolean supports(String contentType);

    T uploadFile(MultipartFile file);

    void delete(T media);

    MediaResponseDTO getMedia(T media);

}
