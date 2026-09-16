package com.tamara.webcv.content.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.validation.SafeText;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillsContentData implements ContentData {

    private String contentType = Constants.SECTION_CONTENT_TYPE_SKILLS;

    @JsonProperty("skillsType")
    @NotNull
    private SkillsContentData.SkillsType skillsType;

    @JsonProperty("isVisible")
    private boolean visible = true;

    @NotEmpty(message = "{validation.notblank}")
    private List<@NotBlank @Size(max = 50, message = "{validation.size}") @SafeText String> textElements;

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
    public enum SkillsType {
        SOFT("soft"),
        TECH("tech");

        private final String type;

        SkillsType(String type) {
            this.type = type;
        }

        @JsonValue
        public String getType() {
            return type;
        }
    }
}
