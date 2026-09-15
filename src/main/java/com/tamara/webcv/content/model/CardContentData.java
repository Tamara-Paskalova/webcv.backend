package com.tamara.webcv.content.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.tamara.webcv.core.Constants;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardContentData implements ContentData { // TODO add tests

    private String contentType = Constants.ABOUT_SECTION_CONTENT_TYPE_CARD;

    @JsonProperty("isVisible")
    private boolean visible = true;

    @NotEmpty(message = "{validation.notblank}")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Map<String, List<Card>> textElements;

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

        List<Card> localizedCards = this.textElements.get(lang);
        return new CardContentData(this.contentType, this.visible, Map.of(lang, localizedCards));
    }
}
