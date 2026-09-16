package com.tamara.webcv.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class MediaController {
    @Autowired
    private MediaServiceFactory factory;

    @PostMapping(value = "/admin/media/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponseDTO> uploadMedia(@PathVariable String type, @RequestParam("file")MultipartFile file) {
        MediaService<? extends MediaEntity> mediaService = factory.get(type);
        MediaResponseDTO responseDTO = mediaService.saveOrUpdate(file, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
