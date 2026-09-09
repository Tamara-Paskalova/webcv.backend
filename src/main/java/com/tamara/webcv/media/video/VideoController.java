package com.tamara.webcv.media.video;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class VideoController {
    @Autowired
    private VideoService service;

    @PostMapping("/admin/video")
    public ResponseEntity<Void> uploadVideo(@Valid @ModelAttribute VideoRequestDTO dto) {
        service.uploadFile(dto.getFile());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @GetMapping("/video")
//    public ResponseEntity<VideoResponseDTO> getVideo() {
//        VideoResponseDTO video = service.getVideo();
//        return ResponseEntity.status(HttpStatus.OK).body(video);
//    }
}
