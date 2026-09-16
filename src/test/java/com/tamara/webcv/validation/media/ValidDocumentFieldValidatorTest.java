package com.tamara.webcv.validation.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Proxy;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidDocumentFieldValidatorTest {

    @Test
    void initializeCallsSuperConfigureWithProperParameters() {
        ValidDocumentFieldValidator validatorSpy = spy(new ValidDocumentFieldValidator());

        long expectedMaxSize = 512000L;
        String[] expectedContentTypes = new String[]{"application/pdf", "pdf"};

        ValidDocument annotationStub = (ValidDocument) Proxy.newProxyInstance(
                ValidDocument.class.getClassLoader(),
                new Class[]{ValidDocument.class},
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
