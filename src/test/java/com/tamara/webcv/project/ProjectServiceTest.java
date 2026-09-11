package com.tamara.webcv.project;

import com.tamara.webcv.exception.ObjectNotFoundException;
import com.tamara.webcv.media.MediaService;
import com.tamara.webcv.media.image.Image;
import com.tamara.webcv.media.image.ImageResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository  repository;

    @Mock
    private MediaService<Image> mediaService;

    @InjectMocks
    private ProjectService service;

    @Test
    void saveProject() {
        String dummyProjectName = "Some name";
        String dummyImageFileName = "imageKey";
        Image image = new Image();
        image.setUniqueFileName(dummyImageFileName);
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setName(dummyProjectName);

        when(mediaService.internalSaveOrUpdate(any(), any(), anyString())).thenReturn(image);

        service.saveProject(dto);
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(repository).save(captor.capture());

        Project project = captor.getValue();

        assertEquals(dummyProjectName, project.getName());
        assertEquals(dummyImageFileName, project.getImage().getUniqueFileName());
    }

    @Test
    void getProjects() {
        List<Project> projects = getProjectList();
        ImageResponseDTO dto = new ImageResponseDTO();
        dto.setId(1L);

        when(repository.findAllByOrderByPriorityDescWithImages()).thenReturn(projects);
        when(mediaService.buildResponseDTO(any())).thenReturn(dto);
        List<ProjectResponseDTO> response = service.getProjects();

        assertEquals(3, response.size());
        assertEquals("first name", response.getFirst().getName());
        assertEquals("third name", response.get(1).getName());
        assertEquals("second name", response.get(2).getName());
        assertEquals(1L, response.getFirst().getImage().getId());
    }

    private List<Project> getProjectList() {
        Image firstImage = new Image();
        firstImage.setId(1L);
        Project firstProject = new Project();
        Project secondProject = new Project();
        Project thirdProject = new Project();
        firstProject.setName("first name");
        firstProject.setPriority(3);
        firstProject.setImage(firstImage);
        secondProject.setName("second name");
        secondProject.setPriority(1);
        thirdProject.setName("third name");
        thirdProject.setPriority(2);
        return List.of(firstProject, thirdProject, secondProject);
    }

    @Test
    void deleteProject() {
        Long id = 1L;
        Project project = new Project();
        Image image = new Image();
        image.setId(id);
        project.setId(id);
        project.setImage(image);

        when(repository.findById(any())).thenReturn(Optional.of(project));

        service.deleteProject(id);

        ArgumentCaptor<Image> imageArgumentCaptor = ArgumentCaptor.forClass(Image.class);
        verify(mediaService).delete(imageArgumentCaptor.capture());
        verify(repository).deleteById(id);

        Image captured = imageArgumentCaptor.getValue();

        assertEquals(id, captured.getId());
    }

    @Test
    void deleteNonExistingProject() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.deleteProject((long) 1));
        verifyNoInteractions(mediaService);
        verify(repository, never()).deleteById(any());
    }
}