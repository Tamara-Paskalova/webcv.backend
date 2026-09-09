package com.tamara.webcv.project;

import com.tamara.webcv.validation.SafeStringList;
import com.tamara.webcv.validation.SafeText;
import com.tamara.webcv.validation.ValidImage;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {

    private boolean isInProgress;

    @NotBlank(message = "{validation.notblank}")
    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String name;

    @NotBlank(message = "{validation.notblank}")
    @Size(max = 250, message = "{validation.size}")
    @SafeText
    private String description;

    @NotEmpty(message = "{validation.notblank}")
    @SafeStringList
    private List<String> technologies;

    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String githubUrl;

    @Size(max = 100, message = "{validation.size}")
    @SafeText
    private String projectUrl;

    @ValidImage
    @NotNull(message = "{validation.notblank}")
    private MultipartFile file;

    @Max(5)
    @Min(1)
    private int priority;
}
