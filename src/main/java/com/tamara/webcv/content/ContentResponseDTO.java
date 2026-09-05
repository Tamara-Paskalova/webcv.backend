package com.tamara.webcv.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDTO {

    private String sectionName;
    private JsonNode contentData; //TODO convert to ContentData
}
