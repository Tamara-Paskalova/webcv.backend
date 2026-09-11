package com.tamara.webcv.media;

import com.tamara.webcv.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class MediaEntity extends BaseEntity {
    @Column(name = "PATH_KEY")
    private String uniqueFileName;

    @Column(name = "METADATA", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private MediaMetadata mediaMetadata;
}
