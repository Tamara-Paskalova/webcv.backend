package com.tamara.webcv.media.cv;

import com.tamara.webcv.media.MediaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Document extends MediaEntity {

    @Column(name = "DOCUMENT_VERSION")
    private int version;
}
