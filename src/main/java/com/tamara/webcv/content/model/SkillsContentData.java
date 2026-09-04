package com.tamara.webcv.content.model;

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
public class SkillsContentData implements ContentData {

    private String contentType = Constants.SECTION_CONTENT_TYPE_SKILLS;
    private boolean isVisible = true;
    @NotEmpty(message = "{validation.notblank}")
    @SafeStringList
    private List<String> skills;

    @Override
    public String getContentType() {
        return this.contentType;
    }
}
