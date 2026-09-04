package com.tamara.webcv.project;

import com.tamara.webcv.core.service.FileStorageService;
import com.tamara.webcv.exception.ObjectNotFoundException;
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
    private FileStorageService storageService;

    @InjectMocks
    private ProjectService service;

    @Test
    void saveProject() {
        String dummyName = "Some name";
        String dummyKey = "imageKey";
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setName(dummyName);

        when(storageService.uploadFile(any(), any())).thenReturn(dummyKey);

        service.saveProject(dto);
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(repository).save(captor.capture());

        Project project = captor.getValue();

        assertEquals(dummyName, project.getName());
        assertEquals(dummyKey, project.getProjectImageKey());

    }

    @Test
    void saveProjectThrowsException() {
        String dummyKey = "imageKey";
        ProjectRequestDTO dto = new ProjectRequestDTO();

        when(storageService.uploadFile(any(), any())).thenReturn(dummyKey);
        when(repository.save(any())).thenThrow(NullPointerException.class);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        assertThrows(NullPointerException.class, () -> service.saveProject(dto));
        verify(storageService).deleteFile(captor.capture());

        String key = captor.getValue();

        assertEquals(dummyKey, key);
    }

    @Test
    void getProjects() {
        Project firstProject = new Project();
        Project secondProject = new Project();
        Project thirdProject = new Project();
        firstProject.setName("first name");
        firstProject.setPriority(3);
        firstProject.setProjectImageKey("imageKey");
        secondProject.setName("second name");
        secondProject.setPriority(1);
        thirdProject.setName("third name");
        thirdProject.setPriority(2);
        List<Project> projects = List.of(firstProject, thirdProject, secondProject);

        when(repository.findAllByOrderByPriorityDesc()).thenReturn(projects);

        List<ProjectResponseDTO> response = service.getProjects();
        verify(storageService).getFileUrl("imageKey");

        assertEquals(3, response.size());
        assertEquals("first name", response.getFirst().getName());
        assertEquals("third name", response.get(1).getName());
        assertEquals("second name", response.get(2).getName());
    }

    @Test
    void deleteProject() {
        Long id = (long) 1;
        Project project = new Project();
        project.setId(id);
        project.setProjectImageKey("imageKey");

        when(repository.findById(any())).thenReturn(Optional.of(project));

        service.deleteProject(id);

        verify(storageService).deleteFile("imageKey");
        verify(repository).deleteById(id);
    }

    @Test
    void deleteNonExistingProject() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.deleteProject((long) 1));
        verifyNoInteractions(storageService);
        verify(repository, never()).deleteById(any());
    }
}