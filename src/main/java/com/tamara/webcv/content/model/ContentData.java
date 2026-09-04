package com.tamara.webcv.content.model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tamara.webcv.core.Constants;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "contentType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = TextContentData.class,
                name = Constants.SECTION_CONTENT_TYPE_TEXT
        ),
        @JsonSubTypes.Type(
                value = SkillsContentData.class,
                name = Constants.SECTION_CONTENT_TYPE_SKILLS
        )
})
public interface ContentData {

    String getContentType();
}
