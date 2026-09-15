package com.tamara.webcv.content.model;

import com.tamara.webcv.validation.SafeStringList;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @NotBlank
    private String title;

    @Nullable
    private String subTitle;

    @Nullable
    private String period;

    @NotNull
    @SafeStringList
    private List<String> textElements;
}
