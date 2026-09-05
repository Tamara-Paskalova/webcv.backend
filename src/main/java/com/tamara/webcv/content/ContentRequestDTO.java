package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequestDTO {

    @NotNull(message = "{content.validation.empty.data}")
    private ContentData contentData;
}
