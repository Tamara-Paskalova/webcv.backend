package com.tamara.webcv.project;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")  //TODO change on deploy
public class ProjectController {
    @Autowired
    private ProjectService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/project")
    public ResponseEntity<Void> uploadProject(@Valid @ModelAttribute ProjectRequestDTO dto) {
        service.saveProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = service.getProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        service.deleteProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
