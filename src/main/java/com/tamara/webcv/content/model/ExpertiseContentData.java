package com.tamara.webcv.content.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.validation.SafeStringList;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertiseContentData implements ContentData {

    private String contentType = Constants.ABOUT_SECTION_CONTENT_TYPE_EXPERTIZE;

    @JsonProperty("expertizeType")
    private ExpertizeType expertizeType;

    @JsonProperty("isVisible")
    private boolean visible = true;

    @NotEmpty(message = "{validation.notblank}")
    @SafeStringList
    private List<String> textElements;

    @JsonIgnore
    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public ContentData getLocalizedContent(String lang) {
        return this;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private enum ExpertizeType {
        CORE("core"),
        SKILLS("skills");

        private final String type;

        ExpertizeType(String type) {
            this.type = type;
        }

        @JsonValue
        public String getType() {
            return type;
        }
    }
}
