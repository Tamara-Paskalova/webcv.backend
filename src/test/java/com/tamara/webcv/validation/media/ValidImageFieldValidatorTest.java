package com.tamara.webcv.validation.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Proxy;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidImageFieldValidatorTest {
    @Test
    void initializeCallsSuperConfigureWithProperParameters() {
        ValidImageFieldValidator validatorSpy = spy(new ValidImageFieldValidator());

        long expectedMaxSize = 10L * 1024 * 1024;
        String[] expectedContentTypes = new String[]{"image/jpeg", "image/png", "image/webp", "image/gif"};

        ValidImage annotationStub = (ValidImage) Proxy.newProxyInstance(
                ValidImage.class.getClassLoader(),
                new Class[]{ValidImage.class},
                (proxy, method, args) -> {
                    if ("maxSizeBytes".equals(method.getName())) return expectedMaxSize;
                    if ("allowedContentTypes".equals(method.getName())) return expectedContentTypes;
                    return method.getDefaultValue();
                }
        );

        validatorSpy.initialize(annotationStub);

        verify(validatorSpy).configure(expectedMaxSize, expectedContentTypes);
    }

}