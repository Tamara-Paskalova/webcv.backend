package com.tamara.webcv.project;

import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProjectService {
    private final String folder = "projectImage";
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private FileStorageService storageService;

    @Transactional
    public void saveProject(ProjectRequestDTO dto) {
        MultipartFile image = dto.getFile();
        String imageKey = storageService.uploadFile(image, folder);
        Project project = toEntity(dto, imageKey);

        try {
            repository.save(project);
        } catch (Exception e) {
            storageService.deleteFile(imageKey);
            throw e;
        }
    }

    public List<ProjectResponseDTO> getProjects() {
        return repository.findAllByOrderByPriorityDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void deleteProject(Long id) {
        Project project = repository.findById(id).orElseThrow(() ->
             new ObjectNotFoundException("Can't find project with Id %s.".formatted(id)));

        String imageKey = project.getProjectImageKey();
        storageService.deleteFile(imageKey);
        repository.deleteById(id);
    }



    private Project toEntity(ProjectRequestDTO dto, String imageKey) {
        Project project = new Project();

        project.setInProgress(dto.isInProgress());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setGithubUrl(dto.getGithubUrl());
        project.setProjectUrl(dto.getProjectUrl());
        project.setTechnologies(dto.getTechnologies());
        project.setProjectImageKey(imageKey);
        project.setPriority(dto.getPriority());

        return project;
    }

    private ProjectResponseDTO toDto(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        String githubUrl = project.getGithubUrl();
        String projectUrl = project.getProjectUrl();
        String imageKey = project.getProjectImageKey();

        if (imageKey != null && !imageKey.isBlank()) {
            dto.setImageUrl(storageService.getFileUrl(imageKey));
        }
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setInProgress(project.isInProgress());
        dto.setTechnologies(project.getTechnologies());
        dto.setPriority(project.getPriority());
        dto.setGithubUrl(githubUrl);
        dto.setProjectUrl(projectUrl);

        return dto;
    }
}
