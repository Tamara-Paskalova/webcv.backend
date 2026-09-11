package com.tamara.webcv.media;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaServiceFactory {
    private final List<MediaService<? extends MediaEntity>> services;

    public MediaServiceFactory(List<MediaService<? extends MediaEntity>> services) {
        this.services = services;
    }

    public MediaService<? extends MediaEntity> get(String type) {
        return services.stream()
                .filter(service -> service.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported media type"));
    }
}
