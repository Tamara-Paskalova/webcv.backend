package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import com.tamara.webcv.content.model.TextContentData;
import com.tamara.webcv.exception.ObjectNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    private final String contentType = "dymmy_content_type";
    private final String sectionName = "section_name";
    private final Set<ConstraintViolation<Object>> violations = new HashSet<>();

    @AfterEach
    void reset() {
        violations.clear();
    }

    @Mock
    private ContentRepository repository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ContentService service;

    @Test
    void saveContent() {
        TextContentData contentData = new TextContentData();
        contentData.setContentType(contentType);
        ContentRequestDTO requestDTO = new ContentRequestDTO();
        requestDTO.setContentData(contentData);

        ArgumentCaptor<ContentData> captorContentData = ArgumentCaptor.forClass(ContentData.class);
        ArgumentCaptor<Content> captorContent = ArgumentCaptor.forClass(Content.class);

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
    void saveContentThrowsExceptionWhenMaliciousContentPassed() {
        ContentData contentData = new TextContentData();
        ContentRequestDTO requestDTO = new ContentRequestDTO(contentData);
        ConstraintViolation violation = Mockito.mock(ConstraintViolation.class);
        violations.add(violation);

        when(validator.validate(any())).thenReturn(violations);

        assertThrows(ConstraintViolationException.class, () -> service.saveContent(requestDTO, sectionName));
    }

    @Test
    void getLocalizedContent() {
        Locale locale = Locale.ENGLISH;
        ContentData contentData = Mockito.mock(ContentData.class);
        Content content = new Content(sectionName, contentData);

        when(repository.findAll()).thenReturn(List.of(content));

        List<ContentResponseDTO> localizedContent = service.getLocalizedContent(locale);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository).findAll();
        verify(contentData).getLocalizedContent(stringArgumentCaptor.capture());

        assertEquals(locale.getLanguage(), stringArgumentCaptor.getValue());
        assertNotNull(localizedContent);
        assertNotNull(localizedContent.getFirst());
        assertNotNull(localizedContent.getFirst().getSectionName());
        assertEquals(sectionName, localizedContent.getFirst().getSectionName());
    }

    @Test
    void updateContent() {
        Content content = new Content();
        content.setSectionName(sectionName);

        when(repository.findBySectionName(any())).thenReturn(Optional.of(content));
        when(validator.validate(any())).thenReturn(violations);

        TextContentData contentData = new TextContentData();
        contentData.setContentType(contentType);
        ContentRequestDTO requestDTO = new ContentRequestDTO(contentData);
        ArgumentCaptor<Content> contentArgumentCaptor = ArgumentCaptor.forClass(Content.class);
        ArgumentCaptor<ContentData> contentDataArgumentCaptor = ArgumentCaptor.forClass(TextContentData.class);
        when(repository.save(any())).thenReturn(content);

        ContentResponseDTO contentResponseDTO = service.updateContent(requestDTO, sectionName);
        verify(validator).validate(contentDataArgumentCaptor.capture());
        verify(repository).save(contentArgumentCaptor.capture());

        assertEquals(contentType, contentDataArgumentCaptor.getValue().getContentType());
        assertEquals(sectionName, contentArgumentCaptor.getValue().getSectionName());
        assertEquals(contentType, contentArgumentCaptor.getValue().getContentData().getContentType());
        assertEquals(sectionName, contentResponseDTO.getSectionName());
        assertEquals(contentType, contentResponseDTO.getContentData().getContentType());
    }

    @Test
    void updateContentThrowsExceptionWhenContentDoesntExist() {
        when(repository.findBySectionName(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.updateContent(new ContentRequestDTO(), sectionName));
    }

    @Test
    void updateContentThrowsExceptionWhenMaliciousDataIsPassed() {
        Content content = new Content();
        ContentData contentData = new TextContentData();
        ContentRequestDTO dto = new ContentRequestDTO(contentData);
        ConstraintViolation violation = Mockito.mock(ConstraintViolation.class);
        violations.add(violation);

        when(validator.validate(any())).thenReturn(violations);
        when(repository.findBySectionName(any())).thenReturn(Optional.of(content));

        assertThrows(ConstraintViolationException.class, () -> service.updateContent(dto, sectionName));
    }

    @Test
    void getContent() {
        TextContentData contentData = new TextContentData();
        contentData.setContentType(contentType);
        Content content = new Content(sectionName, contentData);

        when(repository.findAll()).thenReturn(List.of(content));

        List<ContentResponseDTO> contentResponseDTOS = service.getContent();

        verify(repository).findAll();

        assertNotNull(contentResponseDTOS.getFirst());
        assertEquals(sectionName, contentResponseDTOS.getFirst().getSectionName());
        assertNotNull(contentResponseDTOS.getFirst().getContentData());
        assertEquals(contentType, contentResponseDTOS.getFirst().getContentData().getContentType());
    }
}