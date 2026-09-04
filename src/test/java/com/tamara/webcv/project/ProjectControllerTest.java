package com.tamara.webcv.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService service;

    @InjectMocks
    private ProjectController controller;

    @Test
    void uploadProject() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();

        ResponseEntity<Void> responseEntity = controller.uploadProject(requestDTO);

        verify(service).saveProject(requestDTO);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void getAllProjects() {
        ProjectResponseDTO dto = new ProjectResponseDTO();

        String projectName = "Project Name";
        dto.setName(projectName);
        List<ProjectResponseDTO> responseBody = new ArrayList<>(List.of(dto));

        when(service.getProjects()).thenReturn(responseBody);

        ResponseEntity<List<ProjectResponseDTO>> response = controller.getAllProjects();
        String responseName = response.getBody().getFirst().getName();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectName, responseName);
    }

    @Test
    void deleteProject() {
        Long id = (long) 1;

        ResponseEntity<Void> responseEntity = controller.deleteProject(id);

        verify(service).deleteProject(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}