package com.tamara.webcv.content.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.validation.SafeText;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextContentData implements ContentData {

    private String contentType = Constants.SECTION_CONTENT_TYPE_TEXT;

    @JsonProperty("isVisible")
    private boolean visible = true;

    @NotEmpty(message = "{validation.notblank}")
    private Map<
            @NotBlank @Size(max = 10, message = "{validation.size}") @SafeText String,
            @Valid @NotNull Map<
            @NotBlank @Size(max = 50, message = "{validation.size}") @SafeText String,
            @NotBlank @Size(max = 3000, message = "{validation.size}") @SafeText String>
            > textElements;

    @JsonIgnore
    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public ContentData getLocalizedContent(String lang) {
        if (this.textElements == null) {
            return this;
        }

        Map<String, String> filtered = this.textElements.getOrDefault(lang, this.textElements.get("en"));

        if (filtered == null) {
            filtered = Map.of();
        }

        TextContentData result = new TextContentData();
        result.setTextElements(Map.of(lang, filtered));
        result.setContentType(this.contentType);
        result.setVisible(this.visible);
        return result;
    }
}
