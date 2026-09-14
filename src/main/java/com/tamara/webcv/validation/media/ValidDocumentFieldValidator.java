package com.tamara.webcv.validation.media;

import org.springframework.stereotype.Component;

@Component
public class ValidDocumentFieldValidator extends BaseMediaFieldValidator<ValidDocument> {

    @Override
    public void initialize(ValidDocument annotation) {
        super.configure(annotation.maxSizeBytes(), annotation.allowedContentTypes());
    }

}
