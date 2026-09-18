package com.tamara.webcv.project;

import com.tamara.webcv.core.BaseEntity;
import com.tamara.webcv.media.image.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Project extends BaseEntity {
    @Column(name = "is_in_progress")
    private boolean isInProgress;

    @Column(name = "project_name", nullable = false)
    private String name;

    @Column(name = "project_description", nullable = false)
    private String description;

    @Column(name = "used_technology", columnDefinition = "text[]", nullable = false)
    private List<String> technologies;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "project_url")
    private String projectUrl;

    @OneToOne
    @JoinColumn(name = "image_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Image image;

    @Column(name = "priority", nullable = false)
    private Integer priority;
}
