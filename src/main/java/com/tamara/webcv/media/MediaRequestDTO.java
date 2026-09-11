package com.tamara.webcv.media;

import org.springframework.web.multipart.MultipartFile;

public interface MediaRequestDTO {

    String getContentType();

    MultipartFile getFile();

}
