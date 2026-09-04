package com.tamara.webcv.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequestDTO {

    @NotBlank(message = "{content.validation.empty.type}")
    private String contentType;

    @NotNull(message = "{content.validation.empty.data}")
    private JsonNode contentData;
}
