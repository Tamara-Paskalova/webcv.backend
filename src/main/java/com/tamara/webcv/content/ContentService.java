package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import com.tamara.webcv.exception.ObjectNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContentService {

    @Autowired
    private ContentRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    public ContentResponseDTO saveContent(ContentRequestDTO dto, String sectionName) {
        ContentData contentData = dto.getContentData();

        var violations = validator.validate(contentData);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        Content content = repository.findBySectionName(sectionName).orElse(new Content());

        content.setSectionName(sectionName);
        content.setContentData(contentData);

        Content saved = repository.save(content);

        return toDto(saved);
    }

    public Map<String, ContentData> getLocalizedContent(Locale locale) {
        String lang = locale.getLanguage();

        return repository.findAll().stream()
                .collect(Collectors.toMap(
                        Content::getSectionName,
                        content -> {
                            ContentData rawData = content.getContentData();
                            return rawData.getLocalizedContent(lang);
                        }
                ));
    }

    @Transactional
    public ContentResponseDTO updateContent(ContentRequestDTO dto, String sectionName) {
        Content content = repository.findBySectionName(sectionName).orElseThrow(() ->
                new ObjectNotFoundException("Can't find data for %s section".formatted(sectionName)));

        ContentData currentData = dto.getContentData();

        var violations = validator.validate(currentData);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        content.setContentData(currentData);
        Content updated = repository.save(content);
        return toDto(updated);
    }

    private ContentResponseDTO toDto(Content content) {
        JsonNode jsonNode = objectMapper.valueToTree(content.getContentData());
        return new ContentResponseDTO(content.getSectionName(), jsonNode);

    }
}

