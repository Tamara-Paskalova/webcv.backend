package com.tamara.webcv.media.document;

import com.tamara.webcv.media.MediaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Document extends MediaEntity {

    @Column(name = "document_version")
    private int version;
}
