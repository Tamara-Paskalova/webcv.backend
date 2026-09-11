package com.tamara.webcv.project;

import com.tamara.webcv.media.image.ImageResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private boolean isInProgress;
    private String name;
    private String description;
    private List<String> technologies;
    private String githubUrl;
    private String projectUrl;
    private ImageResponseDTO image;
    private int priority;
}
