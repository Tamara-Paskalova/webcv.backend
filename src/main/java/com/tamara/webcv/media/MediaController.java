package com.tamara.webcv.media;

import com.tamara.webcv.validation.SafeText;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin("*")
public class MediaController {
    @Autowired
    private MediaServiceFactory factory;

    @PostMapping(value = "/admin/media/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponseDTO> uploadMedia(@PathVariable @NotNull @SafeText String type, @RequestParam("file")MultipartFile file) {
        MediaService<? extends MediaEntity> mediaService = factory.get(type);
        MediaResponseDTO responseDTO = mediaService.saveOrUpdate(file, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/media/{type}")
    public ResponseEntity<List<MediaResponseDTO>> getMedia(@PathVariable @NotNull @SafeText String type) {
        MediaService<? extends MediaEntity> mediaService = factory.get(type);
        List<MediaResponseDTO> dtoList = mediaService.getAll(type);
        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }
}
