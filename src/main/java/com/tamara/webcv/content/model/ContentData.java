package com.tamara.webcv.content.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tamara.webcv.core.Constants;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "contentType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = TextContentData.class,
                name = Constants.SECTION_CONTENT_TYPE_TEXT
        ),
        @JsonSubTypes.Type(
                value = CardContentData.class,
                name = Constants.ABOUT_SECTION_CONTENT_TYPE_CARD
        ),
        @JsonSubTypes.Type(
                value = ExpertiseContentData.class,
                name = Constants.ABOUT_SECTION_CONTENT_TYPE_EXPERTIZE
        )
})
public interface ContentData {

    String getContentType();

    ContentData getLocalizedContent(String lang);
}
