package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDTO {

    private String sectionName;
    private ContentData contentData;
}
