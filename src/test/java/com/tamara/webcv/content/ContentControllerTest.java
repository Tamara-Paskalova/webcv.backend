package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    @Mock
    private ContentService service;

    @InjectMocks
    private ContentController controller;

    @Test
    void saveContent() {
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        ContentResponseDTO responseDTO = new ContentResponseDTO();
        String sectionName = "hero_section";
        responseDTO.setSectionName(sectionName);

        ArgumentCaptor<ContentRequestDTO> captorDTO = ArgumentCaptor.forClass(ContentRequestDTO.class);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        when(service.saveContent(any(), any())).thenReturn(responseDTO);
        ResponseEntity<ContentResponseDTO> response = controller.saveContent(sectionName, requestDTO);

        verify(service).saveContent(captorDTO.capture(), captorString.capture());

        assertEquals(requestDTO, captorDTO.getValue());
        assertEquals(sectionName, captorString.getValue());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getContent() {
        Locale locale = Locale.of("en");
        TextContentData contentData = new TextContentData();
        contentData.setContentType("dummy");
        List<ContentResponseDTO> contentList = List.of(new ContentResponseDTO("dummy_section", contentData));
        ArgumentCaptor<Locale> localeArgumentCaptor = ArgumentCaptor.forClass(Locale.class);

        when(service.getLocalizedContent(any())).thenReturn(contentList);

        ResponseEntity<List<ContentResponseDTO>> response = controller.getContent(locale);

        verify(service).getLocalizedContent(localeArgumentCaptor.capture());

        assertEquals(locale, localeArgumentCaptor.getValue());
        assertEquals(contentList, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateContent() {
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        String sectionName = "dummy_section";
        ContentResponseDTO responseDTO = new ContentResponseDTO();
        responseDTO.setSectionName(sectionName);

        ArgumentCaptor<ContentRequestDTO> captorDTO = ArgumentCaptor.forClass(ContentRequestDTO.class);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        when(service.updateContent(any(), any())).thenReturn(responseDTO);

        ResponseEntity<ContentResponseDTO> response = controller.updateContent(sectionName, requestDTO);

        verify(service).updateContent(captorDTO.capture(), captorString.capture());

        assertEquals(sectionName, captorString.getValue());
        assertEquals(requestDTO, captorDTO.getValue());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }
}