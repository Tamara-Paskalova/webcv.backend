package com.tamara.webcv.content.model;

import com.tamara.webcv.validation.SafeText;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @NotBlank
    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String title;

    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String subTitle;

    @Size(max = 15, message = "{validation.size}")
    @SafeText
    private String period;

    @NotNull
    private List<@NotBlank(message = "{validation.notblank}") @Size(max = 500) @SafeText String> textElements;
}
