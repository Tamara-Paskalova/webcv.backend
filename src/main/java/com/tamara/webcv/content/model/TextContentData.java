package com.tamara.webcv.content.model;

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
    private boolean isVisible = true;
    @NotEmpty(message = "{validation.notblank}")
    @SafeMap
    private Map<String, Map<String, String>> textElements;

    @Override
    public String getContentType() {
        return this.contentType;
    }
}
