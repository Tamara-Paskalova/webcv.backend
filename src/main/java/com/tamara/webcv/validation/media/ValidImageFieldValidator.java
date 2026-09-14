package com.tamara.webcv.validation.media;

import org.springframework.stereotype.Component;

@Component
public class ValidImageFieldValidator extends BaseMediaFieldValidator<ValidImage> {

    @Override
    public void initialize(ValidImage annotation) {
        super.configure(annotation.maxSizeBytes(), annotation.allowedContentTypes());
    }
}
