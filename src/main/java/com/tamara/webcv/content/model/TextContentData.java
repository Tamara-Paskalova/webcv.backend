package com.tamara.webcv.content.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.validation.SafeMap;
import jakarta.validation.constraints.NotEmpty;
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
    @SafeMap
    private Map<String, Map<String, String>> textElements;

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

        Map<String, String> filtered = this.textElements.get(lang);

        TextContentData result = new TextContentData();
        result.setTextElements(Map.of(lang, filtered));
        result.setContentType(this.contentType);
        result.setVisible(this.visible);
        return result;
    }
}
