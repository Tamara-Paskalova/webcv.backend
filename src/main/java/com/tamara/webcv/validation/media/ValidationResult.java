package com.tamara.webcv.validation.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValidationResult.Visual.class, name = "visual"),
        @JsonSubTypes.Type(value = ValidationResult.Document.class, name = "document")
})
public sealed interface ValidationResult {

    record Visual(int width, int height) implements ValidationResult {}

    record Document(int pageCount) implements ValidationResult {}
}
