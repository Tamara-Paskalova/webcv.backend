package com.tamara.webcv.content;

import com.tamara.webcv.content.model.TextContentData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    private final String dummySectionName = "dummy_section_name";

    @Mock
    private ContentService service;

    @InjectMocks
    private ContentController controller;

    @Test
    void saveContent() {
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        ContentResponseDTO responseDTO = new ContentResponseDTO();
        responseDTO.setSectionName(dummySectionName);

        ArgumentCaptor<ContentRequestDTO> captorDTO = ArgumentCaptor.forClass(ContentRequestDTO.class);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        when(service.saveContent(any(), any())).thenReturn(responseDTO);
        ResponseEntity<ContentResponseDTO> response = controller.saveContent(dummySectionName, requestDTO);

        verify(service).saveContent(captorDTO.capture(), captorString.capture());

        assertEquals(requestDTO, captorDTO.getValue());
        assertEquals(dummySectionName, captorString.getValue());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getAllLocalizedContent() {
        Locale locale = Locale.of("en");
        TextContentData contentData = new TextContentData();
        contentData.setContentType("dummy");
        List<ContentResponseDTO> contentList = List.of(new ContentResponseDTO(dummySectionName, contentData));
        ArgumentCaptor<Locale> localeArgumentCaptor = ArgumentCaptor.forClass(Locale.class);

        when(service.getLocalizedContent(any())).thenReturn(contentList);

        ResponseEntity<List<ContentResponseDTO>> response = controller.getAllLocalizedContent(locale);

        verify(service).getLocalizedContent(localeArgumentCaptor.capture());

        assertEquals(locale, localeArgumentCaptor.getValue());
        assertEquals(contentList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateContent() {
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        ContentResponseDTO responseDTO = new ContentResponseDTO();
        responseDTO.setSectionName(dummySectionName);

        ArgumentCaptor<ContentRequestDTO> captorDTO = ArgumentCaptor.forClass(ContentRequestDTO.class);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        when(service.updateContent(any(), any())).thenReturn(responseDTO);

        ResponseEntity<ContentResponseDTO> response = controller.updateContent(dummySectionName, requestDTO);

        verify(service).updateContent(captorDTO.capture(), captorString.capture());

        assertEquals(dummySectionName, captorString.getValue());
        assertEquals(requestDTO, captorDTO.getValue());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getAllContent() {
        List<ContentResponseDTO> contentResponseDTOS = List.of(new ContentResponseDTO(dummySectionName, new TextContentData()));

        when(service.getContent()).thenReturn(contentResponseDTOS);

        ResponseEntity<List<ContentResponseDTO>> response = controller.getAllContent();

        verify(service).getContent();

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getFirst());
        assertEquals(dummySectionName, response.getBody().getFirst().getSectionName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}