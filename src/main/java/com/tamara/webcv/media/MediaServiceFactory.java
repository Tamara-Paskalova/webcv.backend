package com.tamara.webcv.media;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MediaServiceFactory {
    private final List<MediaService<? extends MediaEntity>> services;

    public MediaService<? extends MediaEntity> get(String type) {

        if (type == null || type.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Media type parameter cannot be empty");
        }
        return services.stream()
                .filter(service -> service.supports(type))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unsupported media type: " + type
                ));    }
}
