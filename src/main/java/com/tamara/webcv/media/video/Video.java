package com.tamara.webcv.media.video;


import com.tamara.webcv.core.BaseEntity;
import com.tamara.webcv.core.MediaMetadata;
import com.tamara.webcv.media.MediaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "VIDEO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Video extends MediaEntity {

    @Column(name = "VIDEO_VERSION")
    private int videoVersion;
}
