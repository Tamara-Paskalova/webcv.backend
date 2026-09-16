package com.tamara.webcv.validation.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Proxy;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidVideoFieldValidatorTest {
    @Test
    void initializeCallsSuperConfigureWithProperParameters() {
        ValidVideoFieldValidator validatorSpy = Mockito.spy(new ValidVideoFieldValidator());

        long expectedMaxSize = 512000L;
        String[] expectedContentTypes = new String[]{ "mp4", "quicktime", "webm"};

        ValidVideo annotationStub = (ValidVideo) Proxy.newProxyInstance(
                ValidDocument.class.getClassLoader(),
                new Class[]{ValidVideo.class},
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