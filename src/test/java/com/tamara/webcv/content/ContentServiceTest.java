package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import com.tamara.webcv.content.model.TextContentData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository repository;

    @Mock
    private Validator validator;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ContentService service;

    @Test
    void saveContent() {
        TextContentData contentData = new TextContentData();
        String contentType = "dymmy_content_type";
        contentData.setContentType(contentType);
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        requestDTO.setContentData(contentData);
        String sectionName = "section_name";

        ArgumentCaptor<ContentData> captorContentData = ArgumentCaptor.forClass(ContentData.class);
        ArgumentCaptor<Content> captorContent = ArgumentCaptor.forClass(Content.class);

        Set<ConstraintViolation<Object>> violations = new HashSet<>();

        when(validator.validate(any())).thenReturn(violations);
        when(repository.save(any())).thenReturn(new Content(sectionName, contentData));

        ContentResponseDTO response = service.saveContent(requestDTO, sectionName);

        verify(validator).validate(captorContentData.capture());
        verify(repository).save(captorContent.capture());

        assertEquals(sectionName, response.getSectionName());
        assertEquals(contentData, captorContentData.getValue());
        assertEquals(sectionName, captorContent.getValue().getSectionName());
        assertEquals(contentData, captorContent.getValue().getContentData());
    }

    @Test
    void getLocalizedContent() {
    }

    @Test
    void updateContent() {
    }
}