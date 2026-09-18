package com.tamara.webcv.content;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class ContentController {

    @Autowired
    private ContentService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/content/{sectionName}")
    public ResponseEntity<ContentResponseDTO> saveContent(@PathVariable String sectionName, @Valid @RequestBody ContentRequestDTO dto) {
        ContentResponseDTO contentResponseDTO = service.saveContent(dto, sectionName);
        return new ResponseEntity<>(contentResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/content")
    public ResponseEntity<List<ContentResponseDTO>> getAllLocalizedContent(Locale locale) {
        List<ContentResponseDTO> contentResponseDTO = service.getLocalizedContent(locale);
        return new ResponseEntity<>(contentResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/content/all")
    public ResponseEntity<List<ContentResponseDTO>> getAllContent() {
        List<ContentResponseDTO> contentResponseDTOS = service.getContent();
        return new ResponseEntity<>(contentResponseDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/content/{sectionName}")
    public ResponseEntity<ContentResponseDTO> updateContent(@PathVariable String sectionName, @Valid @RequestBody ContentRequestDTO dto) {
        ContentResponseDTO contentResponseDTO = service.updateContent(dto, sectionName);
        return new ResponseEntity<>(contentResponseDTO, HttpStatus.CREATED);
    }

}
