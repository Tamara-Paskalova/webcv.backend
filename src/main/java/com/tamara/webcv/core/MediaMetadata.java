package com.tamara.webcv.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaMetadata {
    private String type;
    private long size;
    private String contentType;
    private String originalFileName;
}
