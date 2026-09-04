package com.tamara.webcv.project;

import com.tamara.webcv.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "PROJECTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Project extends BaseEntity {
    @Column(name = "IS_IN_PROGRESS")
    private boolean isInProgress;

    @Column(name = "PROJECT_NAME", nullable = false)
    private String name;

    @Column(name = "PROJECT_DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "USED_TECHNOLOGIES", columnDefinition = "text[]", nullable = false)
    private List<String> technologies;

    @Column(name = "GITHUB_URL")
    private String githubUrl;

    @Column(name = "PROJECT_URL")
    private String projectUrl;

    @Column(name = "PROJECT_IMAGE_KEY")
    private String projectImageKey;

    @Column(name = "PRIORITY", nullable = false)
    private Integer priority;
}
