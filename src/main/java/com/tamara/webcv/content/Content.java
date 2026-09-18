package com.tamara.webcv.content;

import com.tamara.webcv.content.model.ContentData;
import com.tamara.webcv.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "content")
public class Content extends BaseEntity {
    @Column(name = "section_name", nullable = false, unique = true)
    private String sectionName;

    @Column(name = "section_content", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ContentData contentData;

}
