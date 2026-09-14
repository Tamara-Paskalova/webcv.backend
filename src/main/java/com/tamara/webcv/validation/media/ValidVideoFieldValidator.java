package com.tamara.webcv.validation.media;

import org.springframework.stereotype.Component;

@Component
public class ValidVideoFieldValidator extends BaseMediaFieldValidator<ValidVideo> {

    @Override
    public void initialize(ValidVideo annotation) {
        super.configure(annotation.maxSizeBytes(), annotation.allowedContentTypes());
    }

}
