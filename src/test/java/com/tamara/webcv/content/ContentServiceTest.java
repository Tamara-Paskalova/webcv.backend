package com.tamara.webcv.content;

import com.tamara.webcv.content.model.*;
import com.tamara.webcv.core.Constants;
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

    @Test
    void getContentTypeReturnsCorrectValueForEveryImplementation() {
        TextContentData textContentData = new TextContentData();
        SkillsContentData skillsContentData = new SkillsContentData();
        CardContentData cardContentData = new CardContentData();

        String contentTypeText = textContentData.getContentType();
        String contentTypeSkills = skillsContentData.getContentType();
        String contentTypeCard = cardContentData.getContentType();

        assertEquals(Constants.SECTION_CONTENT_TYPE_TEXT, contentTypeText);
        assertEquals(Constants.SECTION_CONTENT_TYPE_CARD, contentTypeCard);
        assertEquals(Constants.SECTION_CONTENT_TYPE_SKILLS, contentTypeSkills);
    }

    @Test
    void saveContentWithSkillsContentData() {
        SkillsContentData skillsData = new SkillsContentData();
        skillsData.setContentType(Constants.SECTION_CONTENT_TYPE_SKILLS);
        skillsData.setSkillsType(SkillsContentData.SkillsType.SOFT); // Задаем enum
        skillsData.setVisible(true);
        skillsData.setTextElements(List.of("Java", "Spring Boot", "Docker"));

        ContentRequestDTO requestDTO = new ContentRequestDTO();
        requestDTO.setContentData(skillsData);

        ArgumentCaptor<ContentData> captorContentData = ArgumentCaptor.forClass(ContentData.class);
        ArgumentCaptor<Content> captorContent = ArgumentCaptor.forClass(Content.class);

        when(validator.validate(any())).thenReturn(violations);
        when(repository.save(any())).thenReturn(new Content(sectionName, skillsData));

        ContentResponseDTO response = service.saveContent(requestDTO, sectionName);

        verify(validator).validate(captorContentData.capture());
        verify(repository).save(captorContent.capture());

        assertEquals(sectionName, response.getSectionName());
        assertEquals(skillsData, captorContentData.getValue());

        assertInstanceOf(SkillsContentData.class, captorContent.getValue().getContentData());
        SkillsContentData savedData = (SkillsContentData) captorContent.getValue().getContentData();
        assertEquals(SkillsContentData.SkillsType.SOFT, savedData.getSkillsType());
        assertEquals(3, savedData.getTextElements().size());
    }

    @Test
    void updateContentWithSkillsContentData() {
        Content existingContent = new Content();
        existingContent.setSectionName(sectionName);

        when(repository.findBySectionName(any())).thenReturn(Optional.of(existingContent));
        when(validator.validate(any())).thenReturn(violations);

        SkillsContentData newSkillsData = new SkillsContentData();
        newSkillsData.setContentType(Constants.SECTION_CONTENT_TYPE_SKILLS);
        newSkillsData.setSkillsType(SkillsContentData.SkillsType.TECH);
        newSkillsData.setTextElements(List.of("Git", "CI/CD"));

        ContentRequestDTO requestDTO = new ContentRequestDTO(newSkillsData);

        ArgumentCaptor<Content> contentArgumentCaptor = ArgumentCaptor.forClass(Content.class);
        ArgumentCaptor<ContentData> contentDataArgumentCaptor = ArgumentCaptor.forClass(ContentData.class);

        when(repository.save(any())).thenReturn(existingContent);

        ContentResponseDTO response = service.updateContent(requestDTO, sectionName);

        verify(validator).validate(contentDataArgumentCaptor.capture());
        verify(repository).save(contentArgumentCaptor.capture());

        assertInstanceOf(SkillsContentData.class, contentDataArgumentCaptor.getValue());
        assertEquals(SkillsContentData.SkillsType.TECH, ((SkillsContentData) contentDataArgumentCaptor.getValue()).getSkillsType());
        assertEquals(sectionName, contentArgumentCaptor.getValue().getSectionName());

        assertEquals(sectionName, response.getSectionName());
        assertNotNull(response.getContentData());
        assertInstanceOf(SkillsContentData.class, response.getContentData());
    }

    @Test
    void getLocalizedContentWithSkillsContentData() {
        Locale locale = Locale.ENGLISH;
        SkillsContentData mockSkillsData = Mockito.mock(SkillsContentData.class);
        Content content = new Content(sectionName, mockSkillsData);

        when(mockSkillsData.getLocalizedContent(any())).thenReturn(mockSkillsData);
        when(repository.findAll()).thenReturn(List.of(content));

        List<ContentResponseDTO> localizedContent = service.getLocalizedContent(locale);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository).findAll();
        verify(mockSkillsData).getLocalizedContent(stringArgumentCaptor.capture());

        assertEquals(locale.getLanguage(), stringArgumentCaptor.getValue());
        assertNotNull(localizedContent);
        assertFalse(localizedContent.isEmpty());
        assertEquals(sectionName, localizedContent.getFirst().getSectionName());
    }

    @Test
    void saveContentWithCardContentData() {
        Card card = new Card(); // Предполагаем наличие дефолтного конструктора или сеттеров у Card
        CardContentData cardData = new CardContentData();
        cardData.setContentType(Constants.SECTION_CONTENT_TYPE_CARD);
        cardData.setVisible(true);
        cardData.setTextElements(Map.of("en", List.of(card)));

        ContentRequestDTO requestDTO = new ContentRequestDTO();
        requestDTO.setContentData(cardData);

        ArgumentCaptor<ContentData> captorContentData = ArgumentCaptor.forClass(ContentData.class);
        ArgumentCaptor<Content> captorContent = ArgumentCaptor.forClass(Content.class);

        when(validator.validate(any())).thenReturn(violations);
        when(repository.save(any())).thenReturn(new Content(sectionName, cardData));

        ContentResponseDTO response = service.saveContent(requestDTO, sectionName);

        verify(validator).validate(captorContentData.capture());
        verify(repository).save(captorContent.capture());

        assertEquals(sectionName, response.getSectionName());
        assertInstanceOf(CardContentData.class, captorContent.getValue().getContentData());

        CardContentData savedData = (CardContentData) captorContent.getValue().getContentData();
        assertNotNull(savedData.getTextElements());
        assertTrue(savedData.getTextElements().containsKey("en"));
    }

    @Test
    void updateContentWithCardContentData() {
        Content existingContent = new Content();
        existingContent.setSectionName(sectionName);

        when(repository.findBySectionName(any())).thenReturn(Optional.of(existingContent));
        when(validator.validate(any())).thenReturn(violations);

        CardContentData newCardData = new CardContentData();
        newCardData.setContentType(Constants.SECTION_CONTENT_TYPE_CARD);
        newCardData.setTextElements(Map.of("ru", List.of(new Card())));

        ContentRequestDTO requestDTO = new ContentRequestDTO(newCardData);

        ArgumentCaptor<Content> contentArgumentCaptor = ArgumentCaptor.forClass(Content.class);
        when(repository.save(any())).thenReturn(existingContent);

        ContentResponseDTO response = service.updateContent(requestDTO, sectionName);

        verify(repository).save(contentArgumentCaptor.capture());
        assertInstanceOf(CardContentData.class, contentArgumentCaptor.getValue().getContentData());
        assertEquals(sectionName, response.getSectionName());
    }

    @Test
    void getLocalizedContentWithCardContentDataVerifiesLocalizationLogic() {
        Card englishCard = new Card();
        Card russianCard = new Card();

        CardContentData realCardData = new CardContentData();
        realCardData.setContentType(Constants.SECTION_CONTENT_TYPE_CARD);
        realCardData.setVisible(true);
        realCardData.setTextElements(Map.of(
                "en", List.of(englishCard),
                "ru", List.of(russianCard)
        ));

        Content content = new Content(sectionName, realCardData);
        when(repository.findAll()).thenReturn(List.of(content));

        List<ContentResponseDTO> responseRu = service.getLocalizedContent(Locale.forLanguageTag("ru"));

        CardContentData localizedRu = (CardContentData) responseRu.getFirst().getContentData();
        assertTrue(localizedRu.getTextElements().containsKey("ru"));
        assertEquals(russianCard, localizedRu.getTextElements().get("ru").getFirst());

        List<ContentResponseDTO> responseFr = service.getLocalizedContent(Locale.FRANCE);

        CardContentData localizedFr = (CardContentData) responseFr.getFirst().getContentData();
        assertTrue(localizedFr.getTextElements().containsKey("fr")); // метод возвращает Map.of(lang, filtered)
        assertEquals(englishCard, localizedFr.getTextElements().get("fr").getFirst()); // но внутри лежат английские карточки
    }


}