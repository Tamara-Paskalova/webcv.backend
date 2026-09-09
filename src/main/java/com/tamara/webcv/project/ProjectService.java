package com.tamara.webcv.project;

import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.exception.ObjectNotFoundException;
import com.tamara.webcv.media.MediaResponseDTO;
import com.tamara.webcv.media.MediaService;
import com.tamara.webcv.media.image.Image;
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
    private MediaService<Image> mediaService;

    @Transactional
    public void saveProject(ProjectRequestDTO dto) {
        Image image = mediaService.uploadFile(dto.getFile());
        Project project = toEntity(dto, image);

        repository.save(project);
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

        Image image = project.getImage();
        mediaService.delete(image);
        repository.deleteById(id);
    }



    private Project toEntity(ProjectRequestDTO dto, Image image) {
        Project project = new Project();

        project.setInProgress(dto.isInProgress());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setGithubUrl(dto.getGithubUrl());
        project.setProjectUrl(dto.getProjectUrl());
        project.setTechnologies(dto.getTechnologies());
        project.setImage(image);
        project.setPriority(dto.getPriority());

        return project;
    }

    private ProjectResponseDTO toDto(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        String githubUrl = project.getGithubUrl();
        String projectUrl = project.getProjectUrl();
        Image image = project.getImage();

        if (image != null) {
            MediaResponseDTO media = mediaService.getMedia(image);
            dto.setImage(media);
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
