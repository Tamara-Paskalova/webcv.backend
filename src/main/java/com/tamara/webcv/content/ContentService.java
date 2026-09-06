package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import com.tamara.webcv.exception.ObjectNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ContentService {

    @Autowired
    private ContentRepository repository;

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

    public List<ContentResponseDTO> getLocalizedContent(Locale locale) {
        String lang = locale.getLanguage();

        return repository.findAll().stream()
                .map(content -> {
                    ContentData rawData = content.getContentData();
                    ContentData localizedData = rawData.getLocalizedContent(lang);
                    return new Content(content.getSectionName(), localizedData);
                })
                .map(this::toDto)
                .toList();
    }

    public List<ContentResponseDTO> getContent() {
        List<Content> contents = repository.findAll();
        return contents.stream()
                .map(this::toDto)
                .toList();
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
        return new ContentResponseDTO(content.getSectionName(), content.getContentData());

    }
}

